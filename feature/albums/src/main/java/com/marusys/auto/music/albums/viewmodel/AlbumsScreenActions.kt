package com.marusys.auto.music.albums.viewmodel

import com.marusys.auto.music.model.AlbumsSortOption
import com.marusys.auto.music.model.prefs.IsAscending
import com.marusys.auto.music.store.model.album.BasicAlbum

interface AlbumsScreenActions {

    fun changeGridSize(newSize: Int)
    fun changeSortOptions(sortOption: AlbumsSortOption, isAscending: IsAscending)

    fun playAlbums(albumNames: List<BasicAlbum>)
    fun playAlbumsNext(albumNames: List<BasicAlbum>)
    fun addAlbumsToQueue(albumNames: List<BasicAlbum>)

    fun shuffleAlbums(albumNames: List<BasicAlbum>)
    fun shuffleAlbumsNext(albumNames: List<BasicAlbum>)

    fun addAlbumsToPlaylist(albumNames: List<BasicAlbum>, playlistName: String)
}