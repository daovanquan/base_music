package com.marusys.auto.music.store.di

import com.marusys.auto.music.database.di.databaseModule
import com.marusys.auto.music.store.lyrics.LyricsRepository
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import com.marusys.auto.music.store.repository.*
import com.marusys.auto.music.store.repository.impl.MediaRepositoryImpl
import com.marusys.auto.music.store.repository.impl.UserPreferencesRepositoryImpl
import org.koin.dsl.module

val storeModule = module {
    includes(databaseModule)
    single<MediaRepository> { MediaRepositoryImpl() }
    single { AnalyticsRepository(get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl() }
    single { PlaylistsRepository() }
    single { AlbumsRepository() }
    single { PlaylistsRepository() }
    single { TagsRepository(get(), get()) }
    single { QueueRepository() }
    single { LyricsRepository(get() ,get(), get(), get()) }
}