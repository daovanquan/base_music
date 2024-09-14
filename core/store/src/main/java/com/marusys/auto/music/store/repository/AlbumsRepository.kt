package com.marusys.auto.music.store.repository

import com.marusys.auto.music.model.album.BasicAlbumInfo
import com.marusys.auto.music.store.model.album.AlbumSong
import com.marusys.auto.music.store.model.album.AlbumWithSongs
import com.marusys.auto.music.store.model.album.BasicAlbum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class AlbumsRepository : KoinComponent {

    val mediaRepository: MediaRepository by inject()

    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * All the albums of the device alongside their songs
     */
    val albums: StateFlow<List<AlbumWithSongs>> = mediaRepository.songsFlow
        .map {

            val songs = it.songs

            val albumsNames = songs
                .groupBy { song -> song.metadata.albumName }
                .filter { entry -> entry.key != null }

            var counter = 1
            albumsNames.map { entry ->
                val firstSong = entry.value[0]
                AlbumWithSongs(
                    BasicAlbumInfo(
                        counter++,
                        entry.key!!,
                        firstSong.metadata.artistName.orEmpty(),
                        entry.value.size
                    ),
                    entry.value.map { AlbumSong(it, it.metadata.trackNumber) }
                )
            }
        }
        .stateIn(
            scope,
            SharingStarted.Eagerly,
            listOf()
        )

    /**
     * Contains simplified information about all albums
     * Used inside the Albums Screen
     */
    val basicAlbums: StateFlow<List<BasicAlbum>> = albums
        .map { albums -> albums.map { BasicAlbum(it.albumInfo, it.songs.firstOrNull()?.song) } }
        .stateIn(
            scope,
            SharingStarted.Eagerly,
            listOf()
        )

    fun getArtistAlbums(artistName: String) =
        basicAlbums.map { it.filter { album -> album.albumInfo.artist == artistName } }

    fun getAlbumWithSongs(albumId: Int) =
        albums.map { allAlbums ->
            allAlbums
                .firstOrNull { it.albumInfo.id == albumId }
                .let {
                    if (it == null) return@let it
                    // sort the songs by track number
                    val sortedSongs = it.songs.sortedBy { song -> song.trackNumber }
                    it.copy(songs = sortedSongs)
                }
        }

}