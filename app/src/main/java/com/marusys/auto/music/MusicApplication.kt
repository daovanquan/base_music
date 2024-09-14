package com.marusys.auto.music

import android.app.Application
import com.marusys.auto.music.albums.di.albumsModule
import com.marusys.auto.music.playback.di.playbackModule
import com.marusys.auto.music.playlists.di.playlistModule
import com.marusys.auto.music.settings.di.settingsModule
import com.marusys.auto.music.songs.di.songsModule
import com.marusys.auto.music.store.di.storeModule
import com.marusys.auto.music.tageditor.di.tagEditorModule
import com.marusys.auto.music.widgets.di.widgetModule
import com.marusys.auto.music.nowplaying.di.nowPlayingModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
//import com.marusys.auto.music.BuildConfig

class MusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        if (BuildConfig.DEBUG) {
//        }
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@MusicApplication)
            modules(
                storeModule,
                nowPlayingModule,
                playbackModule,
                albumsModule,
                playlistModule,
                settingsModule,
                songsModule,
                tagEditorModule,
                widgetModule
            )
        }
    }

}