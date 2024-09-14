package com.marusys.auto.music.testing.di

import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.testing.playback.TestPlaybackManager
import org.koin.dsl.module

val testPlaybackModule = module {
    single<PlaybackManager> {
        TestPlaybackManager()
    }
}