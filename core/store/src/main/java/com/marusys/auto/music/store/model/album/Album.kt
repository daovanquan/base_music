package com.marusys.auto.music.store.model.album

import com.marusys.auto.music.model.album.BasicAlbumInfo
import com.marusys.auto.music.store.model.song.Song


data class BasicAlbum(
    val albumInfo: BasicAlbumInfo,

    /**
     * Used to get the album cover art
     */
    val firstSong: Song? = null
)

data class AlbumWithSongs(
    val albumInfo: BasicAlbumInfo,
    val songs: List<AlbumSong>
)