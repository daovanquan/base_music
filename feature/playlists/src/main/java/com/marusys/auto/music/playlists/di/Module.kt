package com.marusys.auto.music.playlists.di

import androidx.lifecycle.SavedStateHandle
import com.marusys.auto.music.playlists.CreatePlaylistViewModel
import com.marusys.auto.music.playlists.playlistdetail.PlaylistDetailViewModel
import com.marusys.auto.music.playlists.playlists.PlaylistsViewModel
import com.marusys.auto.music.ui.playlist.AddToPlaylistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playlistModule = module {
    viewModel { PlaylistsViewModel(get(), get()) }
    viewModel { AddToPlaylistViewModel(get()) }
    viewModel { CreatePlaylistViewModel(get()) }
    viewModel { (save: SavedStateHandle) -> PlaylistDetailViewModel(save, get(), get()) }
}