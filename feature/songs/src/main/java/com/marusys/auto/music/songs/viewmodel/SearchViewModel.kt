package com.marusys.auto.music.songs.viewmodel

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.songs.SearchScreenUiState
import com.marusys.auto.music.store.repository.AlbumsRepository
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.model.album.BasicAlbum
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.scope.ScopeViewModel


class SearchViewModel constructor(
    mediaRepository: MediaRepository,
    albumsRepository: AlbumsRepository,
    private val playbackManager: PlaybackManager
) : ScopeViewModel() {

    private val currentQuery = MutableStateFlow("")

    private val _state: StateFlow<SearchScreenUiState> =
        combine(
            currentQuery,
            mediaRepository.songsFlow.map { it.songs },
            albumsRepository.basicAlbums,
            transform = ::getNewState
        ).stateIn(viewModelScope, SharingStarted.Eagerly, SearchScreenUiState.emptyState)

    val state: StateFlow<SearchScreenUiState>
        get() = _state

    private fun getNewState(query: String, songs: List<Song>, albums: List<BasicAlbum>): SearchScreenUiState {
        if (query.isBlank()) return SearchScreenUiState.emptyState
        val filteredSongs = songs.filter { song ->
            song.metadata.title.contains(query, ignoreCase = true)
                    || (song.metadata.albumName?.contains(query, ignoreCase = true) ?: false)
                    || (song.metadata.artistName?.contains(query, ignoreCase = true)
                        ?: false)
        }
        val filteredAlbums = albums.filter { it.albumInfo.name.contains(query, ignoreCase = true) }
        return SearchScreenUiState(query, filteredSongs, filteredAlbums)
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery.value = query
    }

    fun onSongClicked(song: Song, index: Int) {
        val songs = _state.value.songs
        playbackManager.setPlaylistAndPlayAtIndex(songs, index)
    }
}