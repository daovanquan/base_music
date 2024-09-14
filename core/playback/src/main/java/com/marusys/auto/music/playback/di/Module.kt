package com.marusys.auto.music.playback.di

import android.app.NotificationManager
import android.content.ComponentName
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.playback.PlaybackService
import com.marusys.auto.music.playback.activity.ListeningAnalytics
import com.marusys.auto.music.playback.impl.PlaybackManagerImpl
import org.koin.dsl.module

val playbackModule = module {
    factory<SessionToken> { SessionToken(get(), ComponentName(get(), PlaybackService::class.java)) }
    single<PlaybackManager> { PlaybackManagerImpl() }
    single { ListeningAnalytics(get()) }
    single { getSystemService(get(), NotificationManager::class.java) }

}