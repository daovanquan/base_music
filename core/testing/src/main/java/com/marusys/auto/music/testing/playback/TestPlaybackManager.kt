package com.marusys.auto.music.testing.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.marusys.auto.music.model.playback.PlaybackState
import com.marusys.auto.music.model.playback.PlayerState
import com.marusys.auto.music.playback.*
import com.marusys.auto.music.playback.extensions.EXTRA_SONG_ORIGINAL_INDEX
import com.marusys.auto.music.playback.state.MediaPlayerState
import com.marusys.auto.music.store.model.queue.Queue
import com.marusys.auto.music.store.model.queue.QueueItem
import com.marusys.auto.music.store.model.song.Song
import org.koin.core.component.inject
import timber.log.Timber

class TestPlaybackManager: PlaybackManager() {

    private lateinit var mediaController: FakeMediaController

    init {
        initMediaController()
    }
    override val currentSongProgress: Float
        get() = mediaController.currentPosition.toFloat() / mediaController.duration.toFloat()

    override val currentSongProgressMillis
        get() = mediaController.currentPosition

    override val playbackParameters: Pair<Float, Float>
        get() {
            val p = mediaController.playbackParameters
            return p.speed to p.pitch
        }

    override fun clearQueue() {
        mediaController.clearMediaItems()
    }

    /**
     * Toggle the player state
     */
    override fun togglePlayback() {
        mediaController.prepare()
        mediaController.playWhenReady = !mediaController.playWhenReady
    }

    /**
     * Skip forward in currently playing song
     */
    override fun forward() {
        mediaController.sendCustomCommand(
            Commands.JUMP_FORWARD to bundleOf(),
            bundleOf()
        )
    }

    /**
     * Skip backward in currently playing song
     */
    override fun backward() {
        mediaController.sendCustomCommand(
            Commands.JUMP_BACKWARD to bundleOf(),
            bundleOf()
        )
    }

    /**
     * Jumps to the next song in the queue
     */
    override fun playNextSong() {
        mediaController.prepare()
        mediaController.seekToNext()
    }

    /**
     * Jumps to the previous song in the queue
     */
    override fun playPreviousSong() {
        mediaController.prepare()
        mediaController.seekToPrevious()
    }

    override fun playSongAtIndex(index: Int) {
        mediaController.seekTo(index.toLong(), 0)
    }

    override fun removeSongAtIndex(index: Int) {
        mediaController.removeMediaItem(index)
    }

    override fun reorderSong(from: Int, to: Int) {
        mediaController.moveMediaItem(from, to)
    }

    override fun getCurrentSongIndex() = mediaController.currentMediaItemIndex

    override fun setSleepTimer(minutes: Int, finishLastSong: Boolean) {
        mediaController.sendCustomCommand(
            Commands.SET_SLEEP_TIMER to bundleOf(),
            bundleOf(
                "MINUTES" to minutes,
                "FINISH_LAST_SONG" to finishLastSong
            )
        )
    }

    override fun setPlaybackParameters(speed: Float, pitch: Float) {
        mediaController.playbackParameters = FakeMediaController.Parameter(speed, pitch)
    }

    override fun deleteSleepTimer() {
        mediaController.sendCustomCommand(
            Commands.CANCEL_SLEEP_TIMER to Bundle.EMPTY, Bundle.EMPTY
        )
    }

    override fun toggleRepeatMode() {
        mediaController.repeatMode = getRepeatModeFromPlayer(mediaController.repeatMode).next().toPlayer()
    }

    override fun toggleShuffleMode() {
        mediaController.shuffleModeEnabled = !mediaController.shuffleModeEnabled
    }

    override fun seekToPosition(progress: Float) {
        val controller = mediaController
        val songDuration = controller.duration
        controller.seekTo((songDuration * progress).toLong())
    }

    override fun seekToPositionMillis(millis: Long) {
        mediaController.seekTo(millis)
    }

    /**
     * Changes the current playlist of the player and starts playing the song at the specified index
     */
    override fun setPlaylistAndPlayAtIndex(playlist: List<Song>, index: Int) {
        if (playlist.isEmpty()) return
        val mediaItems = playlist.toMediaItems(0)
        stopPlayback() // release everything
        mediaController.apply {
            setMediaItems(mediaItems, index, 0)
            prepare()
            play()
        }
    }

    /** Randomize the order of the list of songs and play */
    override fun shuffle(songs: List<Song>) {
        if (songs.isEmpty()) return
        val shuffled = songs.shuffled()
        stopPlayback()
        mediaController.apply {
            setMediaItems(shuffled.toMediaItems(0), 0, 0)
            prepare()
            play()
        }
    }

    override fun updateToEmptyState() {
        _state.value = MediaPlayerState.empty
    }

    override fun stopPlayback() {
        mediaController.stop()
    }

    override fun shuffleNext(songs: List<Song>) {
        val shuffled = songs.shuffled()
        val currentIndex = mediaController.currentMediaItemIndex
        mediaController.apply {
            addMediaItems(currentIndex + 1, shuffled.toMediaItems(getMaximumOriginalId() + 1))
        }
    }

    override fun playNext(songs: List<Song>) {
        if (songs.isEmpty()) return
        val mediaItems = songs.toMediaItems(getMaximumOriginalId() + 1)
        val currentIndex = mediaController.currentMediaItemIndex
        mediaController.addMediaItems(currentIndex + 1, mediaItems)
        mediaController.prepare()
    }

    override fun addToQueue(songs: List<Song>) {
        val mediaItems = songs.toMediaItems(getMaximumOriginalId() + 1)
        mediaController.addMediaItems(mediaItems)
        mediaController.prepare()
    }

    private fun getMaximumOriginalId(): Int {
        val count = mediaController.mediaItemCount
        if (count == 0) return 0
        return (0 until count).maxOf {
            val mediaItem = mediaController.getMediaItemAt(it)
            mediaItem?.requestMetadata?.extras?.getInt(EXTRA_SONG_ORIGINAL_INDEX) ?: 0
        }
    }

    private fun initMediaController() {
        mediaController = FakeMediaController()
        attachListeners()
    }

    private fun updateState() {
        val currentMediaItem = mediaController.currentMediaItem ?: return updateToEmptyState()
        val songUri = currentMediaItem.requestMetadata.mediaUri ?: return updateToEmptyState()
        val song = mediaRepository.songsFlow.value.getSongByUri(songUri.toString())
            ?: return updateToEmptyState()
        val playbackState = PlaybackState(
            playbackState,
            mediaController.shuffleModeEnabled,
            getRepeatModeFromPlayer(mediaController.repeatMode)
        )
        _state.value = MediaPlayerState(song, playbackState)
    }

    override val playbackState: PlayerState
        get() {
            return when (mediaController.playbackState) {
                Player.STATE_READY -> {
                    if (mediaController.playWhenReady) PlayerState.PLAYING
                    else PlayerState.PAUSED
                }

                Player.STATE_BUFFERING -> PlayerState.BUFFERING
                else -> PlayerState.PAUSED
            }
        }

    private fun attachListeners() {
        mediaController.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                updateState()
                if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                    updateQueue()
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                updateState()
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                updateState()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Timber.d("Media transitioned to ${mediaItem?.requestMetadata?.mediaUri}")
                updateState()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                updateState()
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                updateState()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateState()
            }

        })
    }

    private fun updateQueue() {
        val count = mediaController.mediaItemCount

        val songsLibrary = mediaRepository.songsFlow.value

        if (count <= 0) {
            val q = Queue(listOf())
            queue.value = q
            return
        }

        val queueItems = (0 until count).mapNotNull { i ->
            val mediaItem = mediaController.getMediaItemAt(i) ?: return@mapNotNull null
            val requestMetadata = mediaItem.requestMetadata
            val song = songsLibrary.getSongByUri(requestMetadata.mediaUri.toString())
                ?: return@mapNotNull null
            QueueItem(
                song,
                requestMetadata.extras?.getInt(EXTRA_SONG_ORIGINAL_INDEX, i) ?: i
            )
        }

        val q = Queue(queueItems)
        queue.value = q
    }
}