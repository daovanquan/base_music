package com.marusys.auto.music.ui.playlist

import com.marusys.auto.music.store.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.androidx.scope.ScopeViewModel

class CreatePlaylistViewModel constructor(
    private val playlistsRepository: PlaylistsRepository
) : ScopeViewModel() {


    /**
     * The names of the available playlists
     * Used to prevent the user from creating another list with the same name
     */
    val currentPlaylists: Flow<List<String>> =
        playlistsRepository.playlistsWithInfoFlows
            .map { it.map { playlist -> playlist.name } }


    fun onInsertPlaylist(name: String) {
        playlistsRepository.createPlaylist(name)
    }

}
