package com.marusys.auto.music.settings

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import com.marusys.auto.music.ui.model.AppThemeUi
import com.marusys.auto.music.ui.model.PlayerThemeUi
import com.marusys.auto.music.ui.model.UserPreferencesUi
import com.marusys.auto.music.ui.model.toAppTheme
import com.marusys.auto.music.ui.model.toPlayerTheme
import com.marusys.auto.music.ui.model.toUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel


class SettingsViewModel constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ScopeViewModel(), ISettingsViewModel {

    val state = userPreferencesRepository.userSettingsFlow
        .map { SettingsState.Loaded(it.toUiModel()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState.Loading)

    override fun onFolderDeleted(folder: String) {
        viewModelScope.launch {
            userPreferencesRepository.deleteFolderFromBlacklist(folder)
        }
    }

    override fun onToggleCacheAlbumArt() {
        viewModelScope.launch {
            userPreferencesRepository.toggleCacheAlbumArt()
        }
    }

    override fun onFolderAdded(folder: String) {
        viewModelScope.launch {
            userPreferencesRepository.addBlacklistedFolder(folder)
        }
    }

    override fun onThemeSelected(appTheme: AppThemeUi) {
        viewModelScope.launch {
            userPreferencesRepository.changeTheme(appTheme.toAppTheme())
        }
    }

    override fun onJumpDurationChanged(durationMillis: Int) {
        viewModelScope.launch {
            userPreferencesRepository.changeJumpDurationMillis(durationMillis)
        }
    }

    override fun toggleDynamicColorScheme() {
        viewModelScope.launch {
            userPreferencesRepository.toggleDynamicColor()
        }
    }

    override fun onPlayerThemeChanged(playerTheme: PlayerThemeUi) {
        viewModelScope.launch {
            userPreferencesRepository.changePlayerTheme(playerTheme.toPlayerTheme())
        }
    }

    override fun toggleBlackBackgroundForDarkTheme() {
        viewModelScope.launch {
            userPreferencesRepository.toggleBlackBackgroundForDarkTheme()
        }
    }

    override fun togglePauseVolumeZero() {
        viewModelScope.launch {
            userPreferencesRepository.togglePauseVolumeZero()
        }
    }

    override fun toggleResumeVolumeNotZero() {
        viewModelScope.launch {
            userPreferencesRepository.toggleResumeVolumeNotZero()
        }
    }

    override fun setAccentColor(color: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setAccentColor(color)
        }
    }

    override fun toggleShowExtraControls() {
        viewModelScope.launch {
            userPreferencesRepository.toggleMiniPlayerExtraControls()
        }
    }
}

@Stable
interface ISettingsViewModel {
    fun onFolderDeleted(folder: String)

    fun onToggleCacheAlbumArt()

    fun onFolderAdded(folder: String)

    fun onThemeSelected(appTheme: AppThemeUi)

    fun onJumpDurationChanged(durationMillis: Int)

    fun toggleDynamicColorScheme()

    fun onPlayerThemeChanged(playerTheme: PlayerThemeUi)

    fun toggleBlackBackgroundForDarkTheme()

    fun togglePauseVolumeZero()

    fun toggleResumeVolumeNotZero()

    fun setAccentColor(color: Int)

    fun toggleShowExtraControls()
}

sealed interface SettingsState {
    data object Loading : SettingsState
    data class Loaded(val userPreferences: UserPreferencesUi) : SettingsState
}