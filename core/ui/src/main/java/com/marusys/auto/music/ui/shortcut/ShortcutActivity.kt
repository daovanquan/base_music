package com.marusys.auto.music.ui.shortcut

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.AlbumsRepository
import com.marusys.auto.music.store.repository.PlaylistsRepository
import com.marusys.auto.music.ui.showShortToast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class ShortcutActivity : ComponentActivity(), KoinComponent {

    private val playbackManager: PlaybackManager by inject()

    private val albumsRepository: AlbumsRepository by inject()

    private val playlistsRepository: PlaylistsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent?.extras ?: return finish()

        val command = data.getString(KEY_COMMAND, PLAY_COMMAND)
        val type = data.getString(KEY_TYPE, PLAYLIST_TYPE)

        try {
            if (type == ALBUM_TYPE)
                handleAlbum(command, data.getString(KEY_ID, ""))
            else if (type == PLAYLIST_TYPE)
                handlePlaylist(command, data.getInt(KEY_ID, -1))
        } catch (_: Exception) {

        } finally {
            finish()
        }
    }

    private fun handleAlbum(command: String, albumName: String) {

        if (albumName.isEmpty()) return

        val albumSongs = albumsRepository.albums
            .value.find { it.albumInfo.name == albumName } ?: return

        val songs = albumSongs.songs.map { it.song }
        if (command == SHUFFLE_COMMAND)
            playbackManager.shuffle(songs)
        else if (command == PLAY_COMMAND)
            playbackManager.setPlaylistAndPlayAtIndex(songs)

        showShortToast("$albumName started playing")
    }

    private fun handlePlaylist(command: String, playlist: Int) {

        if (playlist == -1) return

        // we have to delay to give time for the playback manager to connect to the service
        Thread.sleep(100)

        val playlistInfo = runBlocking {
            playlistsRepository.getPlaylistWithSongsFlow(playlist)
                .firstOrNull()
        }

        if (command == SHUFFLE_COMMAND)
            playbackManager.shufflePlaylist(playlist)
        else if (command == PLAY_COMMAND)
            playbackManager.playPlaylist(playlist)

        val name = playlistInfo?.playlistInfo?.name ?: "Playlist"
        showShortToast("$name started playing")
    }

    companion object {
        const val KEY_COMMAND = "COMMAND"
        const val KEY_TYPE = "TYPE"
        const val KEY_ID = "NAME"

        const val PLAY_COMMAND = "PLAY"
        const val SHUFFLE_COMMAND = "SHUFFLE"

        const val ALBUM_TYPE = "ALBUM"
        const val PLAYLIST_TYPE = "PLAYLIST"
    }

}