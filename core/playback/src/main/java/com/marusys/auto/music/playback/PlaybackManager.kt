package com.marusys.auto.music.playback

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.marusys.auto.music.model.playback.PlaybackState
import com.marusys.auto.music.model.playback.PlayerState
import com.marusys.auto.music.playback.extensions.EXTRA_SONG_FILE_PATH
import com.marusys.auto.music.playback.extensions.EXTRA_SONG_ORIGINAL_INDEX
import com.marusys.auto.music.playback.state.MediaPlayerState
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.repository.PlaylistsRepository
import com.marusys.auto.music.store.model.queue.Queue
import com.marusys.auto.music.store.model.queue.QueueItem
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


/**
 * This singleton class represents the interface between the application and the media playback service running in the background.
 * It exposes the current state of the MediaSessionService as state flows so UI can update accordingly
 * It provides methods to manipulate the service, like changing the queue, pausing, rewinding, etc...
 */
abstract class PlaybackManager: PlaylistPlaybackActions, KoinComponent {

    protected val mediaRepository: MediaRepository by inject()
    protected val playlistsRepository: PlaylistsRepository by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    protected val _state = MutableStateFlow(MediaPlayerState.empty)

    private val _currentPlaybackState = PlayerState.PAUSED
    private var currentPosition = 0L
    private var currentPlaybackParameters: Pair<Float, Float> = 1f to 1f

    val state: StateFlow<MediaPlayerState>
        get() = _state

    val queue = MutableStateFlow(Queue.EMPTY)

    abstract val currentSongProgress: Float

    open val currentSongProgressMillis
        get() = currentPosition

    open val playbackParameters: Pair<Float, Float>
        get() {
            return currentPlaybackParameters
        }

    open protected val playbackState: PlayerState get() = _currentPlaybackState


    abstract fun clearQueue()

    /**
     * Toggle the player state
     */
    open fun togglePlayback() {
//        mediaController.prepare()
//        mediaController.playWhenReady = !mediaController.playWhenReady
    }

    /**
     * Skip forward in currently playing song
     */
    abstract fun forward()

    /**
     * Skip backward in currently playing song
     */
    abstract fun backward()

    /**
     * Jumps to the next song in the queue
     */
    abstract fun playNextSong()

    /**
     * Jumps to the previous song in the queue
     */
    abstract fun playPreviousSong()

    abstract fun playSongAtIndex(index: Int)

    abstract fun removeSongAtIndex(index: Int)

    abstract fun reorderSong(from: Int, to: Int)

    abstract fun seekToPosition(progress: Float)

    abstract fun seekToPositionMillis(millis: Long)

    /**
     * Changes the current playlist of the player and starts playing the song at the specified index
     */
    abstract fun setPlaylistAndPlayAtIndex(playlist: List<Song>, index: Int = 0)

    /** Randomize the order of the list of songs and play */
    abstract fun shuffle(songs: List<Song>)

    abstract fun updateToEmptyState()

    abstract fun stopPlayback()

    abstract fun shuffleNext(songs: List<Song>)

    abstract fun playNext(songs: List<Song>)

    abstract fun addToQueue(songs: List<Song>)

    override fun playPlaylist(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                setPlaylistAndPlayAtIndex(songs)
            }
        }
    }

    override fun addPlaylistToNext(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                playNext(songs)
            }
        }
    }

    override fun addPlaylistToQueue(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                addToQueue(songs)
            }
        }
    }

    override fun shufflePlaylist(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            if (songs.isEmpty()) return@launch
            withContext(Dispatchers.Main) {
                shuffle(songs)
            }
        }
    }

    override fun shufflePlaylistNext(playlistId: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val songs = playlistsRepository.getPlaylistSongs(playlistId)
            withContext(Dispatchers.Main) {
                shuffleNext(songs)
            }
        }
    }

    abstract fun getCurrentSongIndex(): Int

    abstract fun setSleepTimer(minutes: Int, finishLastSong: Boolean)

    abstract fun setPlaybackParameters(speed: Float, pitch: Float)

    abstract fun deleteSleepTimer()

    abstract fun toggleRepeatMode()

    abstract fun toggleShuffleMode()

    protected fun Song.toMediaItem(index: Int) =
        MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setArtist(metadata.artistName)
                    .setAlbumTitle(metadata.albumName)
                    .setTitle(metadata.title)
                    .build()
            )
            .setRequestMetadata(
                RequestMetadata.Builder().setMediaUri(uri)
                    .setExtras(bundleOf(EXTRA_SONG_ORIGINAL_INDEX to index, EXTRA_SONG_FILE_PATH to filePath))
                    .build() // to be able to retrieve the URI easily
            )
            .build()

    protected fun List<Song>.toMediaItems(startingIndex: Int) = mapIndexed { index, song ->
        song.toMediaItem(startingIndex + index)
    }

}