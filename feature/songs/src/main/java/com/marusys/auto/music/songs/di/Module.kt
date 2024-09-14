package com.marusys.auto.music.songs.di

import com.marusys.auto.music.songs.viewmodel.SearchViewModel
import com.marusys.auto.music.songs.viewmodel.SongsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val songsModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SongsViewModel(get(), get(), get()) }
}