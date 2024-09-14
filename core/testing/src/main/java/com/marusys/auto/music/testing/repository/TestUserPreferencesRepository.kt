package com.marusys.auto.music.testing.repository

import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.model.AlbumsSortOption
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.model.prefs.*
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import com.marusys.auto.music.testing.data.TestBlacklistDao
import kotlinx.coroutines.flow.*

class TestUserPreferencesRepository: UserPreferencesRepository() {
    override val blacklistDao: BlacklistedFoldersDao = TestBlacklistDao()

    private var userPreferences = UserPreferences(
        librarySettings = LibrarySettings(SongSortOption.TITLE to true, AlbumsSortOption.NAME to true, albumsGridSize = 2, true, listOf()),
        uiSettings = UiSettings(AppTheme.SYSTEM, false, PlayerTheme.BLUR, true, MiniPlayerMode.PINNED),
        playerSettings = PlayerSettings(jumpInterval = 10, false, false)
    )

    private var userStateFlow = MutableStateFlow(userPreferences)

    override val userSettingsFlow: Flow<UserPreferences> get() = userStateFlow

    override val librarySettingsFlow = userSettingsFlow
        .map {
            it.librarySettings
        }.distinctUntilChanged()

    override val playerSettingsFlow = userSettingsFlow
        .map {
            it.playerSettings
        }.distinctUntilChanged()

    override suspend fun changeLibrarySortOrder(songSortOption: SongSortOption, isAscending: Boolean) {
        userPreferences = userPreferences.copy(librarySettings = userPreferences.librarySettings.copy(songsSortOrder = songSortOption to isAscending))
        userStateFlow.emit(userPreferences)
    }

    override suspend fun changeAlbumsGridSize(size: Int) {
        userPreferences = userPreferences.copy(librarySettings = userPreferences.librarySettings.copy(albumsGridSize = size))
        userStateFlow.emit(userPreferences)
    }

    override suspend fun changeAlbumsSortOrder(order: AlbumsSortOption, isAscending: Boolean) {
        userPreferences = userPreferences.copy(librarySettings = userPreferences.librarySettings.copy(albumsSortOrder = order to isAscending))
        userStateFlow.emit(userPreferences)
    }
}