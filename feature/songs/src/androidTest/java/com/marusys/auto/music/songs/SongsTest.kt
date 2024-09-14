package com.marusys.auto.music.songs

import androidx.test.platform.app.InstrumentationRegistry
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.playback.di.playbackModule
import com.marusys.auto.music.songs.di.songsModule
import com.marusys.auto.music.songs.viewmodel.SearchViewModel
import com.marusys.auto.music.songs.viewmodel.SongsViewModel
import com.marusys.auto.music.store.di.storeModule
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.testing.di.databaseTestModule
import com.marusys.auto.music.testing.di.storeTestModule
import com.marusys.auto.music.testing.di.testPlaybackModule
import com.marusys.auto.music.testing.repository.TestMediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

val songTestModule = module {
    single<MediaRepository>(named("test")) { TestMediaRepository() }
    single { SongsViewModel(get(named("test")), get(), get()) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SongsViewModelTest: KoinTest {

    private val mediaRepository: MediaRepository by inject(named("test"))
    private val songsViewModel: SongsViewModel by inject()

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        startKoin {
            androidContext(appContext)
            modules(
                databaseTestModule,
                storeModule,
                playbackModule,
                songTestModule
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun checkViewModel() = runTest {
        val job = launch(UnconfinedTestDispatcher()) {
            songsViewModel.state.collect()
        }
        mediaRepository.onPermissionAccepted()
        val songs = songsViewModel.state.value
        assert(songs is SongsScreenUiState.Success)
        println((songs as SongsScreenUiState.Success).songs)
        assert(songs.songs.isEmpty())
        delay(2000)
        songsViewModel.onSortOptionChanged(SongSortOption.TITLE, false)
        delay(1000)
        println((songs as SongsScreenUiState.Success).songs)
        assert((songsViewModel.state.value as SongsScreenUiState.Success).songs.size == 0)
        job.cancel()
    }
}