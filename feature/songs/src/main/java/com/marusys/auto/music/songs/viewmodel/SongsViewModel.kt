package com.marusys.auto.music.songs.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.songs.SongsScreenUiState
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.store.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel


class SongsViewModel(
    private val mediaRepository: MediaRepository,
    private val mediaPlaybackManager: PlaybackManager,
    private val userPreferencesRepository: UserPreferencesRepository
) : ScopeViewModel() {

    private val sortOptionFlow = userPreferencesRepository.librarySettingsFlow
        .map { it.songsSortOrder }.distinctUntilChanged()

    val state: StateFlow<SongsScreenUiState> =
        mediaRepository.songsFlow
            .map { it.songs }
            .combine(sortOptionFlow) { songList, sortOptionPair ->
                // Sort the list according to the sort option
                println("combine sort option flow $sortOptionPair")
                val ascending = sortOptionPair.second
                val sortedList = if (ascending)
                    songList.sortedByOptionAscending(sortOptionPair.first)
                else
                    songList.sortedByOptionDescending(sortOptionPair.first)
                SongsScreenUiState.Success(sortedList, sortOptionPair.first, sortOptionPair.second)
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SongsScreenUiState.Success(listOf())
            )

    val currentSongUri = mediaPlaybackManager.state.map { it.currentPlayingSong?.uri }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    /**
     * User clicked a song in the list. Default action is to play
     */
    fun onSongClicked(song: Song, index: Int) {
        val songs = (state.value as SongsScreenUiState.Success).songs
        mediaPlaybackManager.setPlaylistAndPlayAtIndex(songs, index)
    }

    fun onPlayNext(songs: List<Song>) {
        mediaPlaybackManager.playNext(songs)
    }

    /**
     * User changed the sorting order of the songs screen
     */
    fun onSortOptionChanged(songSortOption: SongSortOption, isAscending: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.changeLibrarySortOrder(songSortOption, isAscending)
        }
    }

    /**
     * User wants to delete songs.
     * This is only intended for Android versions lower than R, since R and higher have different methods to delete songs.
     * Mainly, in Android R and above, we will have to send an intent to delete a media item and the system will ask the user for permission.
     * So they are implemented as part of the UI in Jetpack Compose
     */
    fun onDelete(songs: List<Song>) {
        mediaRepository.deleteSong(songs[0])
    }

    private fun List<Song>.sortedByOptionAscending(songSortOption: SongSortOption): List<Song> =
        when (songSortOption) {
            SongSortOption.TITLE -> this.sortedBy { it.metadata.title.lowercase() }
            SongSortOption.ARTIST -> this.sortedBy { it.metadata.artistName?.lowercase() }
            SongSortOption.FileSize -> this.sortedBy { it.metadata.sizeBytes }
            SongSortOption.ALBUM -> this.sortedBy { it.metadata.albumName }
            SongSortOption.Duration -> this.sortedBy { it.metadata.durationMillis }
        }


    private fun List<Song>.sortedByOptionDescending(songSortOption: SongSortOption): List<Song> =
        when (songSortOption) {
            SongSortOption.TITLE -> this.sortedByDescending { it.metadata.title.lowercase() }
            SongSortOption.ARTIST -> this.sortedByDescending { it.metadata.artistName?.lowercase() }
            SongSortOption.FileSize -> this.sortedByDescending { it.metadata.sizeBytes }
            SongSortOption.ALBUM -> this.sortedByDescending { it.metadata.albumName }
            SongSortOption.Duration -> this.sortedByDescending { it.metadata.durationMillis }
        }


}