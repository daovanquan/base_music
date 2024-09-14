package com.marusys.auto.music.playlists.playlists

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.playback.PlaylistPlaybackActions
import com.marusys.auto.music.store.repository.PlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.scope.ScopeViewModel


class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    playbackManager: PlaybackManager
): ScopeViewModel(), PlaylistPlaybackActions by playbackManager {


    val state: StateFlow<PlaylistsScreenState> =
        playlistsRepository.playlistsWithInfoFlows
            .map {
                PlaylistsScreenState.Success(it)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PlaylistsScreenState.Loading)


    fun onDelete(id: Int) {
        playlistsRepository.deletePlaylist(id)
    }

    fun onRename(id: Int, name: String) {
        playlistsRepository.renamePlaylist(id, name)
    }

}
