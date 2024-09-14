package com.marusys.auto.music.playback

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.*
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder.UnshuffledShuffleOrder
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.marusys.auto.music.model.prefs.DEFAULT_JUMP_DURATION_MILLIS
import com.marusys.auto.music.model.prefs.PlayerSettings
import com.marusys.auto.music.playback.activity.ListeningAnalytics
import com.marusys.auto.music.playback.extensions.toDBQueueItem
import com.marusys.auto.music.playback.extensions.toMediaItem
import com.marusys.auto.music.playback.timer.SleepTimerManager
import com.marusys.auto.music.playback.timer.SleepTimerManagerListener
import com.marusys.auto.music.playback.volume.AudioVolumeChangeListener
import com.marusys.auto.music.playback.volume.VolumeChangeObserver
import com.marusys.auto.music.store.repository.DBQueueItem
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.repository.QueueRepository
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import timber.log.Timber

class PlaybackService :
    MediaSessionService(),
    SleepTimerManagerListener,
    AudioVolumeChangeListener,
    Player.Listener, KoinScopeComponent {

    override val scope: Scope = createScope(this)
    /*------------------------------ Properties ------------------------------*/
    private val userPreferencesRepository: UserPreferencesRepository by inject()

    private val queueRepository: QueueRepository by inject()

    private val listeningAnalytics: ListeningAnalytics by inject()

    private val mediaRepository: MediaRepository by inject()

    private val notificationManager: NotificationManager by inject()

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var playerSettings: StateFlow<PlayerSettings>

    private lateinit var volumeObserver: VolumeChangeObserver

    private lateinit var sleepTimerManager: SleepTimerManager

    // We use this queue to restore back the original queue when
    // shuffle mode is enabled/disabled
    private var originalQueue: List<MediaItem> = listOf()

    private var currentUsbId: String? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Main)

    /*------------------------------ Methods ------------------------------*/

    private val usbReceiverIntentFilter = IntentFilter().apply {
        addAction(Intent.ACTION_MEDIA_CHECKING)
        addAction(Intent.ACTION_MEDIA_MOUNTED)
        addAction(Intent.ACTION_MEDIA_REMOVED)
        addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        addAction(Intent.ACTION_MEDIA_UNMOUNTED)
        addAction(Intent.ACTION_MEDIA_EJECT)
        addDataScheme("file")
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel() {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Playback Service", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setContentTitle(this.javaClass.simpleName)
            .setContentText("${this.javaClass.simpleName} Running in background")
            .setSmallIcon(R.drawable.baseline_music_24)
        return builder.build()
    }

    private fun initNotification(){
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun ScanUsbAndHandle(){
        Timber.d("======startScanUsb======")
        serviceScope.launch(Dispatchers.IO) {
            mediaRepository.scanSongListFromUsb(currentUsbId!!)
        }

    }
    override fun onCreate() {
        super.onCreate()
        Timber.i("onCreate: ${this.javaClass.simpleName} - $this")

        initNotification()

        player = buildPlayer().apply { addListener(this@PlaybackService) }
        attachAnalyticsListener()

        mediaSession = buildMediaSession()

        sleepTimerManager = SleepTimerManager(this)
        player.addListener(sleepTimerManager)


        playerSettings = userPreferencesRepository.playerSettingsFlow
            .stateIn(
                coroutineScope,
                started = SharingStarted.Eagerly,
                PlayerSettings(
                    DEFAULT_JUMP_DURATION_MILLIS,
                    pauseOnVolumeZero = false,
                    resumeWhenVolumeIncreases = false
                )
            )

        volumeObserver = VolumeChangeObserver(
            applicationContext,
            Handler(Looper.myLooper() ?: Looper.getMainLooper()),
            AudioManager.STREAM_MUSIC
        ).apply { register(this@PlaybackService) }

        recoverQueue()
        coroutineScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(10_000)
                if(player.mediaItemCount > 0) {
                    saveCurrentPosition()
                }
            }
        }
        registerReceiver(usbEventReceiver, usbReceiverIntentFilter)
        getSystemService(UsbManager::class.java).deviceList.keys.firstOrNull()?.let { usb ->
            currentUsbId = usb.drop(7)
            coroutineScope.launch {
                userPreferencesRepository.mountedUsb(usb.drop(7))
            }
        } ?: kotlin.run {
            coroutineScope.launch {
                userPreferencesRepository.unmountedUsb()
            }
        }
        coroutineScope.launch {
            mediaRepository.songsFlow.collect { _ ->
                recoverQueue()
            }
        }
    }

    private val usbEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("action: ${intent.action} data: ${intent.data}")
            when (intent.action) {

                Intent.ACTION_MEDIA_REMOVED -> {
                    Timber.d("ACTION_MEDIA_REMOVED")
                }

                Intent.ACTION_MEDIA_EJECT -> {
                    Timber.d("ACTION_MEDIA_EJECT")
                    val unmountedUsbId = intent.data.toString().drop(7)
                    saveQueue()
                    coroutineScope.launch(Dispatchers.Main) {
                        withContext(Dispatchers.Main) {
                            saveCurrentPosition()
                        }
                        val (lastSongUri, lastPosition) = restorePosition()
                        Timber.d("lastSongUri: $lastSongUri lastPosition: $lastPosition")
                        currentUsbId = null
                        player.clearMediaItems()
                        player.stop()
                        userPreferencesRepository.unmountedUsb(unmountedUsbId)
                    }
//                    mediaSession.player.release()
//                    mediaSession.player.clearMediaItems()
//                    mediaSession.release()
                }

                Intent.ACTION_MEDIA_CHECKING -> {
                    Timber.d("ACTION_MEDIA_CHECKING")
                }

                Intent.ACTION_MEDIA_MOUNTED -> {
                    Timber.d("ACTION_MEDIA_MOUNTED")
                    val newMountedUsbID = intent.data.toString().drop(7)
                    currentUsbId = newMountedUsbID
                    coroutineScope.launch {
                        userPreferencesRepository.mountedUsb(newMountedUsbID)
                    }
                    recoverQueue()
                }

                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    Timber.d("ACTION_USB_DEVICE_ATTACHED")

                }

                Intent.ACTION_MEDIA_UNMOUNTED -> {
                    Timber.d("ACTION_MEDIA_UNMOUNTED")
                }
            }
        }
    }

    private fun attachAnalyticsListener() {
        player.addListener(listeningAnalytics)
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(this, Class.forName("com.marusys.auto.music.MainActivity"))
        intent.action = VIEW_MEDIA_SCREEN_ACTION
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private suspend fun saveCurrentPosition() {
        val uriString = player.currentMediaItem?.requestMetadata?.mediaUri
        val position = player.currentPosition
        withContext(Dispatchers.IO) {
            userPreferencesRepository.saveCurrentPosition(uriString.toString(), position)
        }
    }

    private suspend fun restorePosition() = userPreferencesRepository.getSavedPosition()

    private fun buildCommandButtons(): List<CommandButton> {
        val rewindCommandButton = CommandButton.Builder()
            .setEnabled(true)
            .setDisplayName("Jump Backward")
            .setSessionCommand(SessionCommand(Commands.JUMP_BACKWARD, Bundle()))
            .setIconResId(R.drawable.outline_fast_rewind_24).build()
        val fastForwardCommandButton = CommandButton.Builder()
            .setEnabled(true)
            .setSessionCommand(SessionCommand(Commands.JUMP_FORWARD, Bundle()))
            .setDisplayName("Jump Forward")
            .setIconResId(R.drawable.outline_fast_forward_24).build()
        return listOf(rewindCommandButton, fastForwardCommandButton)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        Timber.i(TAG, "Controller request: ${controllerInfo.packageName}")
        return mediaSession
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun buildMediaSession(): MediaSession {
        return MediaSession
            .Builder(applicationContext, player)
            .setCallback(buildCustomCallback())
            .setCustomLayout(buildCommandButtons())
            .setSessionActivity(buildPendingIntent())
            .build()
    }

    private fun buildPlayer(): ExoPlayer {
        return ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(
                AudioAttributes.Builder().setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA).build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
            }
    }

    override fun onPlayerError(error: PlaybackException) {
        when(error) {
            is ExoPlaybackException -> {
                Timber.e(error, "ExoPlaybackException")

            }
        }
    }

    /**
     * Saves the currently playing queue in the database to retrieve it when starting
     * the application.
     */
    private fun saveQueue() {
        val mediaItems = List(player.mediaItemCount) { player.getMediaItemAt(it) }
        queueRepository.saveQueueFromDBQueueItems(mediaItems.map { it.toDBQueueItem(currentUsbId) }, currentUsbId)
    }

    private fun recoverQueue() {
        if(currentUsbId == null) return
        coroutineScope.launch(Dispatchers.Main) {
            val queue = queueRepository.getQueue(currentUsbId)

            val (lastSongUri, lastPosition) = restorePosition()
            Timber.d("lastSongUri: $lastSongUri lastPosition: $lastPosition")
            val latestSong = queue.firstOrNull { it.songUri.toString() == lastSongUri }
            Timber.d("latestSong $latestSong")
            val songsLibrary = mediaRepository.songsFlow.value
            val recoverQueue = queue.mapNotNull {
                val songs = songsLibrary.getSongByPath(it.filePath) ?: songsLibrary.getSongByUri(it.songUri.toString())
                songs?.let { song ->
                    return@mapNotNull DBQueueItem(
                        song.uri,
                        it.title,
                        it.artist,
                        it.album,
                        song.filePath,
                        currentUsbId
                    )
                }
            }

            val songIndex = if(latestSong != null) recoverQueue.indexOfFirst { it.filePath == latestSong.filePath || it.songUri == latestSong.songUri } else 0

            Timber.d("latestSong index $songIndex")
            player.setMediaItems(
                recoverQueue.mapIndexed { index, item -> item.toMediaItem(index) },
                if (songIndex in recoverQueue.indices) songIndex else 0,
                lastPosition
            )
            player.playWhenReady = true
            player.prepare()
        }
    }


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        if (shuffleModeEnabled) {
            // user enabled shuffle, we have to store the current MediaItems

            val currentMediaItemIndex = player.currentMediaItemIndex
            val originalMediaItems = List(player.mediaItemCount) { i -> player.getMediaItemAt(i) }

            val shuffledQueue = originalMediaItems.toMutableList()
                .shuffled()
                .toMutableList()
                .apply {
                    // remove the current playing media item because we will move it
                    remove(player.getMediaItemAt(currentMediaItemIndex))
                }

            player.moveMediaItem(currentMediaItemIndex, 0)
            player.replaceMediaItems(1, Int.MAX_VALUE, shuffledQueue)
            player.setShuffleOrder(UnshuffledShuffleOrder(player.mediaItemCount))

            originalQueue = originalMediaItems
        } else {

            // user disabled shuffle mode, now we have to restore the original queue
            // and try to maintain the original order

            val currentMediaItemIndex = player.currentMediaItemIndex
            val currentMediaItem = player.getMediaItemAt(currentMediaItemIndex)

            // hashset to determine quickly if a media item was removed when the
            // user had shuffle enabled
            val mediaItemsSet = HashSet<MediaItem>()
            for (i in 0 until player.mediaItemCount) {
                mediaItemsSet.add(player.getMediaItemAt(i))
            }

            val songsBeforeCurrentPlaying = mutableListOf<MediaItem>()
            val songsAfterCurrentPlaying = mutableListOf<MediaItem>()

            var passedCurrentPlaying = false
            for (i in originalQueue) {
                if (i == currentMediaItem)
                    passedCurrentPlaying = true
                else {
                    if (i !in mediaItemsSet) continue
                    if (passedCurrentPlaying)
                        songsAfterCurrentPlaying.add(i)
                    else
                        songsBeforeCurrentPlaying.add(i)
                }
            }

            player.replaceMediaItems(0, currentMediaItemIndex, songsBeforeCurrentPlaying)
            player.replaceMediaItems(player.currentMediaItemIndex + 1, Int.MAX_VALUE, songsAfterCurrentPlaying)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
        if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
            if (player.shuffleModeEnabled) {
                player.setShuffleOrder(UnshuffledShuffleOrder(player.mediaItemCount))
            }
            saveQueue()
        }
    }

    private fun buildCustomCallback(): MediaSession.Callback {
        val customCommands = buildCommandButtons()
        return object : MediaSession.Callback {
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo
            ): MediaSession.ConnectionResult {
                val connectionResult = super.onConnect(session, controller)
                val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
                    .add(SessionCommand(Commands.SET_SLEEP_TIMER, Bundle.EMPTY))
                    .add(SessionCommand(Commands.CANCEL_SLEEP_TIMER, Bundle.EMPTY))
                customCommands.forEach { commandButton ->
                    // Add custom command to available session commands.
                    commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
                }
                return MediaSession.ConnectionResult.accept(
                    availableSessionCommands.build(),
                    connectionResult.availablePlayerCommands
                )
            }

            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle
            ): ListenableFuture<SessionResult> {
                if (Commands.JUMP_FORWARD == customCommand.customAction) {
                    seekForward()
                }
                if (Commands.JUMP_BACKWARD == customCommand.customAction) {
                    seekBackward()
                }
                if (Commands.SET_SLEEP_TIMER == customCommand.customAction) {
                    val minutes = args.getInt("MINUTES", 0)
                    val finishLastSong = args.getBoolean("FINISH_LAST_SONG", false)
                    sleepTimerManager.schedule(minutes, finishLastSong)
                }
                if (Commands.CANCEL_SLEEP_TIMER == customCommand.customAction) {
                    sleepTimerManager.deleteTimer()
                }
                return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
        }

    }


    private var pausedDueToVolume = false
    override fun onVolumeChanged(level: Int) {
        val shouldPause = playerSettings.value.pauseOnVolumeZero
        val shouldResume = playerSettings.value.resumeWhenVolumeIncreases
        if (level < 1 && shouldPause && player.playWhenReady) {
            player.pause()
            if (shouldResume)
                pausedDueToVolume = true
        }
        if (level >= 1 && pausedDueToVolume && shouldResume && !player.playWhenReady) {
            player.play()
            pausedDueToVolume = false
        }
        if (player.playWhenReady) pausedDueToVolume = false
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        // to avoid resuming playback when the headphones disconnect
        if (reason == Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY)
            pausedDueToVolume = false
    }

    fun seekForward() {
        val currentPosition = player.currentPosition
        player.seekTo(currentPosition + playerSettings.value.jumpInterval)
    }

    fun seekBackward() {
        val currentPosition = player.currentPosition
        player.seekTo(currentPosition - playerSettings.value.jumpInterval)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onSleepTimerFinished() {
        player.pause()
    }

    override fun onDestroy() {
        unregisterReceiver(usbEventReceiver)
        coroutineScope.cancel()
        runBlocking {
            saveCurrentPosition()
        }
        mediaSession.run {
            player.release()
            release()
        }
        volumeObserver.unregister()
        super.onDestroy()
    }

    override fun onDeviceInfoChanged(deviceInfo: DeviceInfo) {
        super.onDeviceInfoChanged(deviceInfo)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!player.playWhenReady) {
            quit()
        }
    }

    fun quit(){
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
    }

    companion object {
        const val TAG = "MEDIA_SESSION"
        const val VIEW_MEDIA_SCREEN_ACTION = "MEDIA_SCREEN_ACTION"
        const val NOTIFICATION_CHANNEL_ID = "PlaybackChannelID"
        const val NOTIFICATION_ID = 2468

    }

}