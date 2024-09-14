package com.marusys.auto.music.ui.model

import androidx.compose.runtime.Stable
import com.marusys.auto.music.model.AlbumsSortOption
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.model.prefs.IsAscending
import com.marusys.auto.music.model.prefs.LibrarySettings


@Stable
/**
 * Settings applied to the library and main screen.
 */
data class LibrarySettingsUi(

    /**
     * The order of the songs on the main screen
     */
    val songsSortOrder: Pair<SongSortOption, IsAscending>,

    /**
     * The order of the songs on the main screen
     */
    val albumsSortOrder: Pair<AlbumsSortOption, IsAscending>,

    /**
     * How many columns to show in Albums
     * screen
     */
    val albumsGridSize: Int = 2,

    /**
     * Whether to load the actual album art of the song or
     * to cache album arts of songs of the same album
     */
    val cacheAlbumCoverArt: Boolean,

    /**
     * Files in these folders should not appear in the app
     */
    val excludedFolders: List<String>,

    val usbIdConnected: String? = null
)


fun LibrarySettings.toLibrarySettingsUi() =
    LibrarySettingsUi(
        songsSortOrder, albumsSortOrder, albumsGridSize, cacheAlbumCoverArt, excludedFolders, usbIdConnected
    )