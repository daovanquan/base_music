package com.marusys.auto.music.ui.playlist

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.model.playlist.PlaylistInfo
import com.marusys.auto.music.store.repository.PlaylistsRepository
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.scope.ScopeViewModel

class AddToPlaylistViewModel constructor(
    private val playlistsRepository: PlaylistsRepository
) : ScopeViewModel() {


    val state = playlistsRepository.playlistsWithInfoFlows.map {
        AddToPlaylistState.Success(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AddToPlaylistState.Loading)

    fun addSongsToPlaylists(songs: List<Song>, playlists: List<PlaylistInfo>) {
        playlistsRepository.addSongsToPlaylists(songs.map { it.uri.toString() }, playlists)
    }

}


sealed interface AddToPlaylistState {
    data object Loading : AddToPlaylistState
    data class Success(val playlists: List<PlaylistInfo>) : AddToPlaylistState
}