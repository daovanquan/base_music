package com.marusys.auto.music.albums.viewmodel

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.albums.ui.albumdetail.AlbumDetailActions
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.AlbumsRepository
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.androidx.scope.ScopeViewModel

class AlbumDetailsViewModel(
    albumsRepository: AlbumsRepository,
    albumId: Int,
    private val playbackManager: PlaybackManager
) : ScopeViewModel(), AlbumDetailActions {

    val state: StateFlow<AlbumDetailsScreenState> =
        albumsRepository.getAlbumWithSongs(albumId).map { album ->
            if (album == null) return@map AlbumDetailsScreenState.Loading
            val artistName = album.albumInfo.artist

            val otherAlbums = albumsRepository.getArtistAlbums(artistName)
                .map { it.filter { it.albumInfo.id != albumId } }.firstOrNull() ?: listOf()


            AlbumDetailsScreenState.Loaded(album, otherAlbums)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AlbumDetailsScreenState.Loading)


    override fun play() {
        playbackManager.setPlaylistAndPlayAtIndex(getAlbumSongs())
    }

    override fun playAtIndex(index: Int) {
        playbackManager.setPlaylistAndPlayAtIndex(getAlbumSongs(), index)
    }

    override fun playNext() {
        playbackManager.playNext(getAlbumSongs())
    }

    override fun shuffle() {
        playbackManager.shuffle(getAlbumSongs())
    }

    override fun shuffleNext() {
        playbackManager.shuffleNext(getAlbumSongs())
    }

    override fun addToQueue() {
        playbackManager.addToQueue(getAlbumSongs())
    }

    private fun getAlbumSongs(): List<Song> {
        return (state.value as? AlbumDetailsScreenState.Loaded)?.albumWithSongs?.songs?.map { it.song }
            ?: listOf()
    }

    companion object {
        const val ALBUM_ID_KEY = "ALBUM_ID"
    }

}