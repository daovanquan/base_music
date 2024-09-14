package com.marusys.auto.music.songs

import androidx.compose.runtime.Immutable
import com.marusys.auto.music.model.SongSortOption
import com.marusys.auto.music.store.model.song.Song


sealed interface SongsScreenUiState {

    @Immutable
    data class Success(
        val songs: List<Song>,
        val songSortOption: SongSortOption = SongSortOption.TITLE,
        val isSortedAscendingly: Boolean = true
    ) : SongsScreenUiState
}