package com.marusys.auto.music.testing.repository

import com.marusys.auto.music.model.song.BasicSongMetadata
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.store.model.song.SongLibrary
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import timber.log.Timber

class TestMediaRepository : MediaRepository() {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var mediaSyncJob: Job? = null

    private val songs = MutableSharedFlow<SongLibrary>()
    override val songsFlow = callbackFlow {

        permissionListener = PermissionListener {

            mediaSyncJob = launch {
                send(getAllSongs())
                mediaSyncJob = null
            }

        }

        // Initial Sync
        mediaSyncJob = launch {
            mediaSyncJob = launch {
                try {
                    send(getAllSongs())
                } catch (e: Exception) {
                    Timber.e(e.message)
                } finally {
                    mediaSyncJob = null
                }
            }
        }

        awaitClose{
            mediaSyncJob?.cancel()
        }
    }.combine(
            userPreferencesRepository.librarySettingsFlow.map { it.excludedFolders }
            ) { songs: List<Song>, excludedFolders: List<String> ->

        val filteredSongs = songs.filter { song ->
            !excludedFolders.any { folder ->
                song.filePath.startsWith(folder)
            }
        }

        SongLibrary(filteredSongs)
    }.flowOn(Dispatchers.IO).stateIn(scope, SharingStarted.Eagerly, SongLibrary(emptyList()))

    override suspend fun getAllSongs(): List<Song> {
        println("Getting all songs")
        return mutableListOf(
            Song("song1", 1, BasicSongMetadata("D Song 1", "", "", 0, 0)),
            Song("song2", 2, BasicSongMetadata("B Song 2", "", "", 0, 0)),
            Song("song3", 3, BasicSongMetadata("A Song 3", "", "", 0, 0)),
            Song("song4", 4, BasicSongMetadata("C Song 4", "", "", 0, 0)),
        )
    }

    override fun onPermissionAccepted() {
        scope.launch {
            songs.emit(SongLibrary(getAllSongs()))
        }
    }
}