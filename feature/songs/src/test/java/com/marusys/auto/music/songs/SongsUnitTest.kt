package com.marusys.auto.music.songs

import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.songs.viewmodel.SongsViewModel
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.testing.MainDispatcherRule
import com.marusys.auto.music.testing.di.*
import com.marusys.auto.music.testing.repository.TestMediaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test

val songTestModule = module {
    single<MediaRepository> { TestMediaRepository() }
    single { SongsViewModel(get(), get(), get()) }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SongsViewModelTest: KoinTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val songsViewModel: SongsViewModel by inject()
    private val testMediaRepository: MediaRepository by inject()

    @Before
    fun setUp() {
        startKoin {
            modules(
                databaseUnitTestModule,
                storeUnitTestModule,
                testPlaybackModule,
                songTestModule
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun test_GetSongs() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { songsViewModel.state.collect() }
        val screenUiState = songsViewModel.state.value
        assert(screenUiState is SongsScreenUiState.Success)
        println((screenUiState as SongsScreenUiState.Success).songs)
        assert(screenUiState.songs.size == 4)
        assert(screenUiState.songs.first().metadata.title == "A Song 3")
        songsViewModel.onSortOptionChanged(SongSortOption.TITLE, false)
        testMediaRepository.onPermissionAccepted()
        delay(500)
        assert((songsViewModel.state.value as SongsScreenUiState.Success).songs.size == 4)
        val sortedSongs = (songsViewModel.state.value as SongsScreenUiState.Success).songs
        println(sortedSongs)
        assert(sortedSongs.first().metadata.title == "D Song 1")
        job.cancel()
    }
}