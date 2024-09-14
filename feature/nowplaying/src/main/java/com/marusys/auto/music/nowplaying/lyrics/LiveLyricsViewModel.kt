package com.marusys.auto.music.nowplaying.lyrics

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.network.data.NetworkMonitor
import com.marusys.auto.music.network.model.NetworkStatus
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.lyrics.LyricsRepository
import com.marusys.auto.music.store.lyrics.LyricsResult
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.scope.ScopeViewModel

class LiveLyricsViewModel constructor(
    private val playbackManager: PlaybackManager,
    private val lyricsRepository: LyricsRepository,
    private val networkMonitor: NetworkMonitor
) : ScopeViewModel() {


    private val _state = MutableStateFlow<LyricsScreenState>(LyricsScreenState.Loading)
    val state: StateFlow<LyricsScreenState>
        get() = _state

    init {
        viewModelScope.launch {
            playbackManager.state.distinctUntilChanged { old, new -> old.currentPlayingSong == new.currentPlayingSong }
                .collect {
                    if (it.currentPlayingSong == null) {
                        _state.value = LyricsScreenState.NotPlaying
                    } else {
                        loadLyrics(it.currentPlayingSong!!)
                    }
                }
        }
        viewModelScope.launch {
            networkMonitor.state.collect {
                if (it == NetworkStatus.CONNECTED)
                    onRegainedNetworkConnection()
            }
        }
    }

    private fun onRegainedNetworkConnection() {
        val currentState = _state.value
        if (currentState is LyricsScreenState.NoLyrics && currentState.reason == NoLyricsReason.NETWORK_ERROR) {
            onRetry()
        }
    }

    fun onRetry() {
        val currentSong = (playbackManager.state.value.currentPlayingSong) ?: return
        viewModelScope.launch {
            loadLyrics(currentSong)
        }
    }

    private suspend fun loadLyrics(song: Song) = withContext(Dispatchers.Default) {
        _state.value = LyricsScreenState.SearchingLyrics

        val lyricsResult = lyricsRepository
            .getLyrics(
                song.uri,
                song.metadata.title,
                song.metadata.albumName.orEmpty(),
                song.metadata.artistName.orEmpty(),
                song.metadata.durationMillis.toInt() / 1000
            )

        val newState = when (lyricsResult) {
            is LyricsResult.NotFound ->
                LyricsScreenState.NoLyrics(NoLyricsReason.NOT_FOUND)

            is LyricsResult.NetworkError ->
                LyricsScreenState.NoLyrics(NoLyricsReason.NETWORK_ERROR)

            is LyricsResult.FoundPlainLyrics ->
                LyricsScreenState.TextLyrics(lyricsResult.plainLyrics, lyricsResult.lyricsSource)

            is LyricsResult.FoundSyncedLyrics ->
                LyricsScreenState.SyncedLyrics(lyricsResult.syncedLyrics, lyricsResult.lyricsSource)
        }

        if (isActive)
            _state.value = newState
    }

    fun songProgressMillis(): Long {
        return playbackManager.currentSongProgressMillis
    }

    fun setSongProgressMillis(millis: Long) {
        return playbackManager.seekToPositionMillis(millis)
    }

    fun saveExternalLyricsToSongFile() {
        viewModelScope.launch {
            val currentPlayingSongUri = playbackManager.state.value.currentPlayingSong ?: return@launch
            lyricsRepository.saveExternalLyricsToSongFile(
                currentPlayingSongUri.uri,
                currentPlayingSongUri.metadata.title,
                currentPlayingSongUri.metadata.albumName.orEmpty(),
                currentPlayingSongUri.metadata.artistName.orEmpty()
            )
        }
    }

}