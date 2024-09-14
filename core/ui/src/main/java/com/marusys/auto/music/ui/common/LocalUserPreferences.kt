package com.marusys.auto.music.ui.common

import androidx.compose.runtime.compositionLocalOf
import com.marusys.auto.music.ui.model.UserPreferencesUi


val LocalUserPreferences = compositionLocalOf<UserPreferencesUi> { throw IllegalArgumentException("Not provided") }