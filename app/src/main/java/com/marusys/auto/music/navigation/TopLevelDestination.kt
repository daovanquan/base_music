package com.marusys.auto.music.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.marusys.auto.music.playlists.navigation.PLAYLISTS_NAVIGATION_GRAPH
import com.marusys.auto.music.settings.navigation.SETTINGS_NAVIGATION_GRAPH
import com.marusys.auto.music.albums.navigation.ALBUMS_NAVIGATION_GRAPH
import com.marusys.auto.music.nowplaying.navigation.QUEUE_NAVIGATION_GRAPH
import com.marusys.auto.music.nowplaying.navigation.QUEUE_ROUTE
import com.marusys.auto.music.songs.navigation.SONGS_NAVIGATION_GRAPH

enum class TopLevelDestination(
    val iconSelected: ImageVector,
    val iconNotSelected: ImageVector,
    val title: String,
    val route: String
) {
    SONGS(
        Icons.Rounded.MusicNote,
        Icons.Outlined.MusicNote,
        "Songs",
        SONGS_NAVIGATION_GRAPH
    ),


    PLAYLISTS(
        Icons.Rounded.LibraryMusic,
        Icons.Outlined.LibraryMusic,
        "Playlists",
        PLAYLISTS_NAVIGATION_GRAPH
    ),


    ALBUMS(
        Icons.Rounded.Album,
        Icons.Outlined.Album,
        "Albums",
        ALBUMS_NAVIGATION_GRAPH
    ),


    SETTINGS(
        Icons.Rounded.Settings,
        Icons.Outlined.Settings,
        "Settings",
        SETTINGS_NAVIGATION_GRAPH
    ),

    QUEUE(
        Icons.Rounded.Queue,
        Icons.Outlined.Queue,
        "Queue",
        QUEUE_ROUTE
    )


}