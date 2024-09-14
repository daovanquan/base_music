package com.marusys.auto.music.testing.di

import com.marusys.auto.music.store.repository.UserPreferencesRepository
import com.marusys.auto.music.store.repository.impl.UserPreferencesRepositoryImpl
import com.marusys.auto.music.testing.repository.TestUserPreferencesRepository
import org.koin.dsl.module

val storeTestModule = module {
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl() }
}
val storeUnitTestModule = module {
    single<UserPreferencesRepository> { TestUserPreferencesRepository() }
}