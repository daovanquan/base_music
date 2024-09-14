package com.marusys.auto.music.albums.di

import com.marusys.auto.music.albums.viewmodel.AlbumDetailsViewModel
import com.marusys.auto.music.albums.viewmodel.AlbumsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val albumsModule = module {
    viewModel { AlbumsViewModel(get(), get(), get(), get()) }
    viewModel { params -> AlbumDetailsViewModel(get(), params[0], get()) }
}