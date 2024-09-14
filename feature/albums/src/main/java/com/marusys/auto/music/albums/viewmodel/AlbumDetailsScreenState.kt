package com.marusys.auto.music.albums.viewmodel

import androidx.compose.runtime.Stable
import com.marusys.auto.music.store.model.album.AlbumWithSongs
import com.marusys.auto.music.store.model.album.BasicAlbum


sealed interface AlbumDetailsScreenState {
    data object Loading : AlbumDetailsScreenState

    @Stable
    data class Loaded(
        val albumWithSongs: AlbumWithSongs,
        val otherAlbums: List<BasicAlbum>
    ) : AlbumDetailsScreenState
}