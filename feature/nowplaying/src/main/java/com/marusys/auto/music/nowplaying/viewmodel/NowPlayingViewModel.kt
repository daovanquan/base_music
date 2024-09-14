package com.marusys.auto.music.nowplaying.viewmodel

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.nowplaying.NowPlayingState
import kotlinx.coroutines.flow.*
import org.koin.androidx.scope.ScopeViewModel

class NowPlayingViewModel constructor(
    private val playbackManager: PlaybackManager,
) : ScopeViewModel(), INowPlayingViewModel {

    private val _showQueue: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showQueue: StateFlow<Boolean>
        get() = _showQueue

    private val _state: StateFlow<NowPlayingState> =
        playbackManager.state
            .map { mediaPlayerState ->
                val song = mediaPlayerState.currentPlayingSong
                    ?: return@map NowPlayingState.NotPlaying
                NowPlayingState.Playing(
                    song,
                    mediaPlayerState.playbackState.playerState,
                    repeatMode = mediaPlayerState.playbackState.repeatMode,
                    isShuffleOn = mediaPlayerState.playbackState.isShuffleOn
                )
            }.stateIn(viewModelScope, SharingStarted.Eagerly, NowPlayingState.NotPlaying)


    val state: StateFlow<NowPlayingState>
        get() = _state

    override fun currentSongProgress() = playbackManager.currentSongProgress

    override fun togglePlayback() {
        playbackManager.togglePlayback()
    }

    override fun nextSong() {
        playbackManager.playNextSong()
    }

    override fun jumpForward() {
        playbackManager.forward()
    }

    override fun jumpBackward() {
        playbackManager.backward()
    }

    override fun onUserSeek(progress: Float) {
        playbackManager.seekToPosition(progress)
    }

    override fun previousSong() {
        playbackManager.playPreviousSong()
    }

    override fun toggleRepeatMode() {
        playbackManager.toggleRepeatMode()
    }

    override fun toggleShuffleMode() {
        playbackManager.toggleShuffleMode()
    }

    override fun showQueue() {
        _showQueue.value = !_showQueue.value
    }

    override fun closeQueue() {
        _showQueue.value = false
    }
}