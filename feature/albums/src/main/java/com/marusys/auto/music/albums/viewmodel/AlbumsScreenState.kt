package com.marusys.auto.music.albums.viewmodel

import com.marusys.auto.music.store.model.album.BasicAlbum


data class AlbumsScreenState(
    val albums: List<BasicAlbum>,
    val selectedAlbum: BasicAlbum? = null
)
