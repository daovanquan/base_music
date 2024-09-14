package com.marusys.auto.music.model.prefs



data class UserPreferences(

    //setting thuộc tính
    val librarySettings: LibrarySettings,

    val uiSettings: UiSettings,

    val playerSettings: PlayerSettings

)



enum class AppTheme {
    LIGHT, DARK, SYSTEM
}