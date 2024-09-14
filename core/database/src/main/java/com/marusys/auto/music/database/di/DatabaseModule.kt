package com.marusys.auto.music.database.di

import android.content.Context
import androidx.room.Room
import com.marusys.auto.music.database.MusicaDatabase
import com.marusys.auto.music.database.entities.DB_NAME
import com.marusys.auto.music.database.migrations.MIGRATION_3_4
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), MusicaDatabase::class.java, name = DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
    single {get<MusicaDatabase>().playlistsDao() }
    single {get<MusicaDatabase>().blacklistDao() }
    single {get<MusicaDatabase>().queueDao() }
    single {get<MusicaDatabase>().activityDao() }
    single {get<MusicaDatabase>().lyricsDao() }
}