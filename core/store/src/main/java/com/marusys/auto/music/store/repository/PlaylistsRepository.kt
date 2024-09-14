package com.marusys.auto.music.store.repository

import com.marusys.auto.music.database.dao.PlaylistDao
import com.marusys.auto.music.database.entities.playlist.PlaylistEntity
import com.marusys.auto.music.database.model.PlaylistInfoWithNumberOfSongs
import com.marusys.auto.music.model.playlist.PlaylistInfo
import com.marusys.auto.music.store.model.playlist.Playlist
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class PlaylistsRepository: KoinComponent {

    private val playlistsDao: PlaylistDao by inject()
    private val mediaRepository: MediaRepository by inject()

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val playlistsWithInfoFlows =
        playlistsDao.getPlaylistsInfoFlow()
            .map {
                it.toDomainPlaylists()
            }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), listOf())


    fun createPlaylist(name: String) {
        coroutineScope.launch {
            val playlist = PlaylistEntity(name = name)
            playlistsDao.createPlaylist(playlist)
        }
    }

    fun createPlaylistAndAddSongs(name: String, songUris: List<String>) {
        coroutineScope.launch {
            playlistsDao.createPlaylistAndAddSongs(name, songUris)
        }
    }

    fun addSongsToPlaylists(songsUris: List<String>, playlists: List<PlaylistInfo>) {
        coroutineScope.launch {
            playlistsDao.insertSongsToPlaylists(songsUris, playlists.toDBEntities())
        }
    }

    fun deletePlaylist(id: Int) {
        coroutineScope.launch {
            playlistsDao.deletePlaylistWithSongs(id)
        }
    }

    fun renamePlaylist(id: Int, newName: String) {
        coroutineScope.launch {
            playlistsDao.renamePlaylist(id, newName)
        }
    }

    fun removeSongsFromPlaylist(id: Int, songsUris: List<String>) {
        coroutineScope.launch {
            playlistsDao.removeSongsFromPlaylist(id, songsUris)
        }
    }

    suspend fun getPlaylistSongs(id: Int): List<Song> {
        val songUris = playlistsDao.getPlaylistSongs(id)
        val songLibrary = (mediaRepository.songsFlow as StateFlow).value
        return songLibrary.getSongsByUris(songUris)
    }


    fun getPlaylistWithSongsFlow(playlistId: Int): Flow<Playlist> =
        combine(
            mediaRepository.songsFlow as StateFlow,
            playlistsDao.getPlaylistWithSongsFlow(playlistId)
        ) { library, playlistWithSongs ->

            // Convert the songs to a map to enable fast retrieval
            val songsSet = library.songs.associateBy { it.uri.toString() }

            // The uris of the song
            val playlistSongsUriStrings = playlistWithSongs.songUris

            val playlistSongs = mutableListOf<Song>()
            for (uriString in playlistSongsUriStrings.map { it.songUriString }) {
                val song = songsSet[uriString]
                if (song != null) {
                    playlistSongs.add(song)
                }
            }

            val playlistInfo = playlistWithSongs.playlistEntity
            Playlist(
                PlaylistInfo(playlistInfo.id, playlistInfo.name, playlistSongs.size),
                playlistSongs
            )
        }


    private fun PlaylistInfo.toDBEntity() =
        PlaylistEntity(id, name)

    private fun List<PlaylistInfo>.toDBEntities() =
        map { it.toDBEntity() }

    private fun PlaylistInfoWithNumberOfSongs.toDomainPlaylist() =
        PlaylistInfo(
            id = playlistEntity.id,
            name = playlistEntity.name,
            numberOfSongs = numberOfSongs
        )

    private fun List<PlaylistInfoWithNumberOfSongs>.toDomainPlaylists() =
        map { it.toDomainPlaylist() }


}