package com.marusys.auto.music.playlists.playlists

import com.marusys.auto.music.model.playlist.PlaylistInfo

sealed interface PlaylistsScreenState {

    data object Loading : PlaylistsScreenState
    data class Success(val playlists: List<PlaylistInfo>) : PlaylistsScreenState

}