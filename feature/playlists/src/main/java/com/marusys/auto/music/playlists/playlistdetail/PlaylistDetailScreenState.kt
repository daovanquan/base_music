package com.marusys.auto.music.playlists.playlistdetail

import com.marusys.auto.music.store.model.song.Song

sealed interface PlaylistDetailScreenState {

    data object Loading : PlaylistDetailScreenState

    data class Loaded(
        val id: Int,
        val name: String,
        val songs: List<Song>,
        val numberOfSongs: Int = songs.size
    ) : PlaylistDetailScreenState

    data object Deleted : PlaylistDetailScreenState

}