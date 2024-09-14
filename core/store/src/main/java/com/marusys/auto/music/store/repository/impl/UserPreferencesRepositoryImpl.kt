package com.marusys.auto.music.store.repository.impl

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.database.entities.prefs.BlacklistedFolderEntity
import com.marusys.auto.music.model.AlbumsSortOption
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.model.prefs.*
import com.marusys.auto.music.store.preferences.datastore
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import timber.log.Timber

class UserPreferencesRepositoryImpl: UserPreferencesRepository() {
    private val context: Context by inject()
    override val blacklistDao: BlacklistedFoldersDao by inject()
    private var usbId: String? = null

    override val userSettingsFlow: Flow<UserPreferences> =
        combine(
            context.datastore.data.catch { emptyPreferences() },
            blacklistDao.getAllBlacklistedFoldersFlow()
        ) { settings, blacklistFolders ->
            usbId = settings[USB_MOUNTED_ID]
            mapPrefsToModel(settings, blacklistFolders)
        }

    override val librarySettingsFlow = userSettingsFlow
        .map {
            it.librarySettings
        }.distinctUntilChanged()

    override val playerSettingsFlow = userSettingsFlow
        .map {
            it.playerSettings
        }.distinctUntilChanged()


    override suspend fun saveCurrentPosition(songUriString: String, position: Long) {
        Timber.d("Saving position: $position for song: $songUriString")
        context.datastore.edit {
            it[SONG_URI_KEY] = songUriString
            it[SONG_POSITION_KEY] = position
        }
    }

    override suspend fun getSavedPosition(): Pair<String?, Long> {
        val prefs = context.datastore.data.first()
        val songUri = prefs[SONG_URI_KEY]
        val songPosition = prefs[SONG_POSITION_KEY] ?: 0
        return songUri to songPosition
    }

    override suspend fun changeLibrarySortOrder(songSortOption: SongSortOption, isAscending: Boolean) {
        context.datastore.edit {
            it[SONGS_SORT_ORDER_KEY] = "${songSortOption}:$isAscending"
        }
    }

    override suspend fun changeAlbumsSortOrder(order: AlbumsSortOption, isAscending: Boolean) {
        context.datastore.edit {
            it[ALBUMS_SORT_ORDER_KEY] = "${order}:$isAscending"
        }
    }

    override suspend fun changeAlbumsGridSize(size: Int) {
        context.datastore.edit {
            it[ALBUMS_GRID_SIZE_KEY] = size
        }
    }

    override suspend fun changeTheme(appTheme: AppTheme) {
        context.datastore.edit {
            it[THEME_KEY] = appTheme.toString()
        }
    }

    override suspend fun setAccentColor(color: Int) {
        context.datastore.edit {
            it[ACCENT_COLOR_KEY] = color
        }
    }

    override suspend fun changePlayerTheme(playerTheme: PlayerTheme) {
        context.datastore.edit {
            it[PLAYER_THEME_KEY] = playerTheme.toString()
        }
    }

    override suspend fun changeJumpDurationMillis(duration: Int) {
        context.datastore.edit {
            it[JUMP_DURATION_KEY] = duration
        }
    }

    override suspend fun toggleBoolean(key: Preferences.Key<Boolean>, default: Boolean) {
        context.datastore.edit {
            it[key] = !(it[key] ?: !default)
        }
    }

    override suspend fun unmountedUsb(usbId: String?) {
        if(usbId == null || this.usbId == null || usbId == this.usbId) {
            context.datastore.edit {
                it.remove(USB_MOUNTED_ID)
            }
        }
    }

    override suspend fun mountedUsb(newMountedUsbID: String) {
        context.datastore.edit {
            it[USB_MOUNTED_ID] = newMountedUsbID
        }
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

        val albumsGridSize = this[ALBUMS_GRID_SIZE_KEY] ?: 1

        val songsSortOrder = if (songSortOptionsParts == null)
            SongSortOption.TITLE to true else SongSortOption.valueOf(songSortOptionsParts[0]) to songSortOptionsParts[1].toBoolean()

        val albumsSortOrder = if (albumsSortOptionsParts == null)
            AlbumsSortOption.NAME to true else AlbumsSortOption.valueOf(albumsSortOptionsParts[0]) to albumsSortOptionsParts[1].toBoolean()


        val cacheAlbumCoverArt = this[CACHE_ALBUM_COVER_ART_KEY] ?: true
        val usbMountId = this[USB_MOUNTED_ID]

        return LibrarySettings(
            songsSortOrder, albumsSortOrder, albumsGridSize, cacheAlbumCoverArt, excludedFolders, usbMountId
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
}