package com.marusys.auto.music.database

import androidx.room.Room
import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.database.entities.DB_NAME
import com.marusys.auto.music.database.entities.prefs.BlacklistedFolderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.junit.MockitoJUnitRunner

val databaseTestModule = module {
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

@RunWith(MockitoJUnitRunner::class)
class DatabaseUnitTest: KoinTest {
}