package com.marusys.auto.music.playlists.playlistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.PlaylistsRepository
import com.marusys.auto.music.store.model.song.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel

class PlaylistDetailViewModel constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistDao: PlaylistsRepository,
    private val playbackManager: PlaybackManager
) : ScopeViewModel(), PlaylistActions {

    private val _state =
        MutableStateFlow<PlaylistDetailScreenState>(PlaylistDetailScreenState.Loading)
    val state: StateFlow<PlaylistDetailScreenState> get() = _state

    private var collectionJob: Job

    private val id: String = savedStateHandle.get<String>("id")
        ?: throw IllegalArgumentException("Playlist Id not given")

    init {

        collectionJob = viewModelScope.launch {
            playlistDao.getPlaylistWithSongsFlow(id.toInt())
                .collect {
                    _state.emit(
                        PlaylistDetailScreenState.Loaded(
                            it.playlistInfo.id,
                            it.playlistInfo.name,
                            it.songs
                        )
                    )
                }
        }

    }

    fun onSongClicked(song: Song) {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        val index = songs.indexOf(song)
        if (index == -1) return


        playbackManager.setPlaylistAndPlayAtIndex(songs, index)
    }

    override fun removeSongs(songUris: List<String>) {
        playlistDao.removeSongsFromPlaylist(id.toInt(), songUris)
    }

    override fun delete() {
        collectionJob.cancel()
        playlistDao.deletePlaylist(id.toInt())
        _state.value = PlaylistDetailScreenState.Deleted
    }

    override fun playNext() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.playNext(songs)
    }

    override fun addToQueue() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.addToQueue(songs)
    }

    override fun shuffle() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.shuffle(songs)
    }

    override fun shuffleNext() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.shuffleNext(songs)
    }

    override fun rename(newName: String) {
        playlistDao.renamePlaylist(id.toInt(), newName)
    }

    override fun play() {
        val state = _state.value
        if (state !is PlaylistDetailScreenState.Loaded) return

        val songs = state.songs
        playbackManager.setPlaylistAndPlayAtIndex(songs)
    }


}

interface PlaylistActions {

    fun play()
    fun shuffle()
    fun playNext()
    fun shuffleNext()
    fun rename(newName: String)
    fun addToQueue()
    fun delete()
    fun removeSongs(songUris: List<String>)

}