package com.marusys.auto.music.albums.viewmodel

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.model.AlbumsSortOption
import com.marusys.auto.music.model.prefs.IsAscending
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.AlbumsRepository
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.model.album.BasicAlbum
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel

class AlbumsViewModel(
    private val albumsRepository: AlbumsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val playbackManager: PlaybackManager,
    private val mediaRepository: MediaRepository
) : ScopeViewModel(), AlbumsScreenActions {


    private val _state: StateFlow<AlbumsScreenState> = albumsRepository.basicAlbums
        .combine(
            userPreferencesRepository.librarySettingsFlow.map { it.albumsSortOrder }
                .stateIn(viewModelScope, SharingStarted.Eagerly, AlbumsSortOption.NAME to true)
        ) { albums, sortOption ->
            val sortedAlbums = albums
                .let {
                    val sortProperty: Comparator<BasicAlbum> = when (sortOption.first) {
                        AlbumsSortOption.NAME -> compareBy { it.albumInfo.name }
                        AlbumsSortOption.ARTIST -> compareBy { it.albumInfo.artist }
                        AlbumsSortOption.NUMBER_OF_SONGS -> compareBy { it.albumInfo.numberOfSongs }
                    }
                    if (sortOption.second)
                        it.sortedWith(sortProperty)
                    else
                        it.sortedWith(sortProperty).reversed()
                }
            AlbumsScreenState(sortedAlbums)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AlbumsScreenState(albumsRepository.basicAlbums.value)
        )

    val state: StateFlow<AlbumsScreenState>
        get() = _state


    override fun changeGridSize(newSize: Int) {
        viewModelScope.launch {
            userPreferencesRepository.changeAlbumsGridSize(newSize)
        }
    }

    override fun changeSortOptions(sortOption: AlbumsSortOption, isAscending: IsAscending) {
        viewModelScope.launch {
            userPreferencesRepository.changeAlbumsSortOrder(sortOption, isAscending)
        }
    }

    override fun playAlbums(albumNames: List<BasicAlbum>) {
        val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
        playbackManager.setPlaylistAndPlayAtIndex(songs, 0)
    }

    override fun playAlbumsNext(albumNames: List<BasicAlbum>) {
        val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
        playbackManager.playNext(songs)
    }

    override fun addAlbumsToQueue(albumNames: List<BasicAlbum>) {
        val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
        playbackManager.addToQueue(songs)
    }

    override fun shuffleAlbums(albumNames: List<BasicAlbum>) {
        val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
        playbackManager.shuffle(songs)
    }

    override fun shuffleAlbumsNext(albumNames: List<BasicAlbum>) {
        val songs = getAlbumsSongs(albumNames.map { it.albumInfo.name })
        playbackManager.shuffleNext(songs)
    }

    override fun addAlbumsToPlaylist(albumNames: List<BasicAlbum>, playlistName: String) {
        TODO("Not yet implemented")
    }

    private fun getAlbumSongs(albumName: String): List<Song> {
        return mediaRepository.songsFlow.value.songs
            .filter { it.metadata.albumName == albumName }
    }

    private fun getAlbumsSongs(albumNames: List<String>): List<Song> {
        return albumNames
            .map { getAlbumSongs(it) }
            .flatten()
    }

}