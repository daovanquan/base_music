package com.marusys.auto.music.ui.model

import androidx.compose.runtime.Stable
import com.marusys.auto.music.model.prefs.UserPreferences


@Stable
data class UserPreferencesUi(
    val librarySettings: LibrarySettingsUi,

    val uiSettings: UiSettingsUi,

    val playerSettings: PlayerSettingsUi
)

fun UserPreferences.toUiModel() =
    UserPreferencesUi(
        librarySettings.toLibrarySettingsUi(),
        uiSettings.toUiSettingsUi(),
        playerSettings.toPlayerSettingsUi()
    )