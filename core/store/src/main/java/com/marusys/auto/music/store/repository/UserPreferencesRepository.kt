package com.marusys.auto.music.store.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.database.entities.prefs.BlacklistedFolderEntity
import com.marusys.auto.music.model.AlbumsSortOption
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.model.prefs.AppTheme
import com.marusys.auto.music.model.prefs.DEFAULT_ACCENT_COLOR
import com.marusys.auto.music.model.prefs.DEFAULT_JUMP_DURATION_MILLIS
import com.marusys.auto.music.model.prefs.LibrarySettings
import com.marusys.auto.music.model.prefs.MiniPlayerMode
import com.marusys.auto.music.model.prefs.PlayerSettings
import com.marusys.auto.music.model.prefs.PlayerTheme
import com.marusys.auto.music.model.prefs.UiSettings
import com.marusys.auto.music.model.prefs.UserPreferences
import com.marusys.auto.music.store.preferences.datastore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class UserPreferencesRepository : KoinComponent{

    open val userSettingsFlow: Flow<UserPreferences> = flowOf()

    abstract val blacklistDao: BlacklistedFoldersDao

    abstract val librarySettingsFlow: Flow<LibrarySettings>

    abstract val playerSettingsFlow: Flow<PlayerSettings>


    open suspend fun saveCurrentPosition(songUriString: String, position: Long) {}

    open suspend fun getSavedPosition(): Pair<String?, Long> {
        return "" to 0
    }

    open suspend fun changeLibrarySortOrder(songSortOption: SongSortOption, isAscending: Boolean) {

    }

    open suspend fun changeAlbumsSortOrder(order: AlbumsSortOption, isAscending: Boolean) {

    }

    open suspend fun changeAlbumsGridSize(size: Int) {

    }

    open suspend fun changeTheme(appTheme: AppTheme) {

    }

    suspend fun toggleBlackBackgroundForDarkTheme() {
        toggleBoolean(BLACK_BACKGROUND_FOR_DARK_THEME_KEY)
    }

    open suspend fun setAccentColor(color: Int) {

    }

    open suspend fun changePlayerTheme(playerTheme: PlayerTheme) {
    }

    suspend fun toggleCacheAlbumArt() {
        toggleBoolean(CACHE_ALBUM_COVER_ART_KEY)
    }

    suspend fun toggleDynamicColor() {
        toggleBoolean(DYNAMIC_COLOR_KEY)
    }

    suspend fun togglePauseVolumeZero() {
        toggleBoolean(PAUSE_IF_VOLUME_ZERO)
    }

    suspend fun toggleMiniPlayerExtraControls() {
        toggleBoolean(MINI_PLAYER_EXTRA_CONTROLS)
    }

    suspend fun toggleResumeVolumeNotZero() {
        toggleBoolean(RESUME_IF_VOLUME_INCREASED)
    }

    suspend fun deleteFolderFromBlacklist(folder: String) = withContext(Dispatchers.IO) {
        blacklistDao.deleteFolder(folder)
    }

    suspend fun addBlacklistedFolder(folder: String) = withContext(Dispatchers.IO) {
        blacklistDao.addFolder(BlacklistedFolderEntity(0, folder))
    }

    open suspend fun changeJumpDurationMillis(duration: Int) {
    }

    open protected suspend fun toggleBoolean(key: Preferences.Key<Boolean>, default: Boolean = true) {

    }


    private fun Preferences.getPlayerSettings(): PlayerSettings {
        val jumpDuration = this[JUMP_DURATION_KEY] ?: DEFAULT_JUMP_DURATION_MILLIS
        val pauseOnVolumeZero = this[PAUSE_IF_VOLUME_ZERO] ?: false
        val resumeOnVolumeNotZero = this[RESUME_IF_VOLUME_INCREASED] ?: false
        return PlayerSettings(
            jumpDuration,
            pauseOnVolumeZero,
            resumeOnVolumeNotZero
        )
    }

    private fun Preferences.getUiSettings(): UiSettings {
        val theme = AppTheme.valueOf(this[THEME_KEY] ?: "DARK")
        val isUsingDynamicColor = this[DYNAMIC_COLOR_KEY] ?: false
        val playerTheme = PlayerTheme.valueOf(this[PLAYER_THEME_KEY] ?: "BLUR")
        val blackBackgroundForDarkTheme = this[BLACK_BACKGROUND_FOR_DARK_THEME_KEY] ?: true
        val accentColor = this[ACCENT_COLOR_KEY] ?: DEFAULT_ACCENT_COLOR
        val miniPlayerExtraControls = this[MINI_PLAYER_EXTRA_CONTROLS] ?: true
        return UiSettings(
            theme,
            isUsingDynamicColor,
            playerTheme,
            blackBackgroundForDarkTheme,
            MiniPlayerMode.PINNED,
            accentColor,
            miniPlayerExtraControls
        )
    }

    private fun Preferences.getLibrarySettings(excludedFolders: List<String>): LibrarySettings {
        val songSortOptionsParts = this[SONGS_SORT_ORDER_KEY]?.split(":")
        val albumsSortOptionsParts = this[ALBUMS_SORT_ORDER_KEY]?.split(":")

        val albumsGridSize = this[ALBUMS_GRID_SIZE_KEY] ?: 2

        val songsSortOrder = if (songSortOptionsParts == null)
            SongSortOption.TITLE to true else SongSortOption.valueOf(songSortOptionsParts[0]) to songSortOptionsParts[1].toBoolean()

        val albumsSortOrder = if (albumsSortOptionsParts == null)
            AlbumsSortOption.NAME to true else AlbumsSortOption.valueOf(albumsSortOptionsParts[0]) to albumsSortOptionsParts[1].toBoolean()


        val cacheAlbumCoverArt = this[CACHE_ALBUM_COVER_ART_KEY] ?: true

        return LibrarySettings(
            songsSortOrder, albumsSortOrder, albumsGridSize, cacheAlbumCoverArt, excludedFolders
        )
    }

    private fun mapPrefsToModel(
        prefs: Preferences,
        blacklistedFolders: List<BlacklistedFolderEntity>
    ) = UserPreferences(
        prefs.getLibrarySettings(blacklistedFolders.map { it.folderPath }),
        prefs.getUiSettings(),
        prefs.getPlayerSettings()
    )

    open suspend fun unmountedUsb(usbId: String? = null) {

    }

    open suspend fun mountedUsb(newMountedUsbID: String) {

    }

    companion object {
        val SONGS_SORT_ORDER_KEY = stringPreferencesKey("SONGS_SORT")
        val ALBUMS_SORT_ORDER_KEY = stringPreferencesKey("ALBUMS_SORT")
        val THEME_KEY = stringPreferencesKey("THEME")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("DYNAMIC_COLOR")
        val PLAYER_THEME_KEY = stringPreferencesKey("PLAYER_THEME")
        val BLACK_BACKGROUND_FOR_DARK_THEME_KEY =
            booleanPreferencesKey("BLACK_BACKGROUND_FOR_DARK_THEME")
        val CACHE_ALBUM_COVER_ART_KEY = booleanPreferencesKey("CACHE_ALBUM_COVER_ART")
        val JUMP_DURATION_KEY = intPreferencesKey("JUMP_DURATION_KEY")
        val SONG_URI_KEY = stringPreferencesKey("SONG_URI")
        val SONG_POSITION_KEY = longPreferencesKey("SONG_POSITION")
        val PAUSE_IF_VOLUME_ZERO = booleanPreferencesKey("PAUSE_VOLUME_ZERO")
        val RESUME_IF_VOLUME_INCREASED = booleanPreferencesKey("RESUME_IF_VOLUME_INCREASED")
        val ACCENT_COLOR_KEY = intPreferencesKey("ACCENT_COLOR")
        val MINI_PLAYER_EXTRA_CONTROLS = booleanPreferencesKey("MINI_PLAYER_EXTRA_CONTROLS")
        val ALBUMS_GRID_SIZE_KEY = intPreferencesKey("ALBUMS_GRID_SIZE")
        val USB_MOUNTED_ID = stringPreferencesKey("USB_MOUNTED_ID")
    }

}