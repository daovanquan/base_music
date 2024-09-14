package com.marusys.auto.music.nowplaying.queue

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.PlaylistsRepository
import com.marusys.auto.music.store.model.queue.Queue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.scope.ScopeViewModel

class QueueViewModel constructor(
    private val playlistsRepository: PlaylistsRepository,
    private val playbackManager: PlaybackManager
) : ScopeViewModel() {


    val queueScreenState =
        combine(playbackManager.queue, playbackManager.state) { queue, playerState ->
            if (queue.items.isEmpty()) return@combine QueueScreenState.Loading
            val currentPlayingIndex = playbackManager.getCurrentSongIndex()
            val currentSongId = queue.items[currentPlayingIndex].originalIndex
            QueueScreenState.Loaded(queue, currentPlayingIndex, currentSongId)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(500, 500), QueueScreenState.Loading
        )

    fun onSongClicked(index: Int) {
        playbackManager.playSongAtIndex(index)
    }

    fun onRemoveFromQueue(index: Int) {
        playbackManager.removeSongAtIndex(index)
    }

    fun reorderSong(from: Int, to: Int) {
        playbackManager.reorderSong(from, to)
    }

    fun onClearQueue() {
        playbackManager.clearQueue()
    }

    fun onSaveAsPlaylist(name: String) {
        val songs =
            (queueScreenState.value as QueueScreenState.Loaded).queue.items.map { it.song.uri.toString() }
        playlistsRepository.createPlaylistAndAddSongs(name, songs)
    }
}


sealed interface QueueScreenState {

    data class Loaded(
        val queue: Queue,
        val currentSongIndex: Int,
        val currentSongId: Int
    ) : QueueScreenState

    data object Loading : QueueScreenState
}