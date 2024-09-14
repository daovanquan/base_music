package com.marusys.auto.music.store.model.playlist

import com.marusys.auto.music.model.playlist.PlaylistInfo
import com.marusys.auto.music.store.model.song.Song


data class Playlist(
    val playlistInfo: PlaylistInfo,
    val songs: List<Song>
)