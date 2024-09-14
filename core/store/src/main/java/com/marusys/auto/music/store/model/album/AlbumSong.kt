package com.marusys.auto.music.store.model.album

import com.marusys.auto.music.store.model.song.Song


data class AlbumSong(
    val song: Song,
    val trackNumber: Int? = null
)