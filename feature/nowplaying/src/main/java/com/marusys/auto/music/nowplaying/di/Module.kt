package com.marusys.auto.music.nowplaying.di

import com.marusys.auto.music.network.data.LyricsSource
import com.marusys.auto.music.network.di.retrofitModule
import com.marusys.auto.music.nowplaying.lyrics.LiveLyricsViewModel
import com.marusys.auto.music.nowplaying.queue.QueueViewModel
import com.marusys.auto.music.nowplaying.speed.PlaybackSpeedViewModel
import com.marusys.auto.music.nowplaying.timer.SleepTimerViewModel
import com.marusys.auto.music.nowplaying.viewmodel.NowPlayingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val nowPlayingModule = module {
    includes(retrofitModule)
    single { LyricsSource() }
    viewModel { NowPlayingViewModel(get()) }
    viewModel { SleepTimerViewModel(get()) }
    viewModel { PlaybackSpeedViewModel(get()) }
    viewModel { QueueViewModel(get(), get()) }
    viewModel { LiveLyricsViewModel(get(), get(), get()) }
}