package com.marusys.auto.music.settings.di

import com.marusys.auto.music.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(get()) }
}