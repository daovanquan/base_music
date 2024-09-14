package com.marusys.auto.music.testing.di

import androidx.room.Room
import com.marusys.auto.music.database.MusicaDatabase
import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.database.entities.DB_NAME
import com.marusys.auto.music.testing.data.TestBlacklistDao
import org.koin.dsl.module

val databaseTestModule = module {
    single {
        Room.inMemoryDatabaseBuilder(get(), MusicaDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }
    single {get<MusicaDatabase>().playlistsDao() }
    single {get<MusicaDatabase>().blacklistDao() }
    single {get<MusicaDatabase>().queueDao() }
    single {get<MusicaDatabase>().activityDao() }
    single {get<MusicaDatabase>().lyricsDao() }
}
val databaseUnitTestModule = module {
    single<BlacklistedFoldersDao> { TestBlacklistDao() }
}