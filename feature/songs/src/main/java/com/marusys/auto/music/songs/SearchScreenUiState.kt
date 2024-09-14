package com.marusys.auto.music.songs

import androidx.compose.runtime.Immutable
import com.marusys.auto.music.store.model.album.BasicAlbum
import com.marusys.auto.music.store.model.song.Song


@Immutable
data class SearchScreenUiState(
    val searchQuery: String,
    val songs: List<Song>,
    val albums: List<BasicAlbum>
) {
    companion object {
        val emptyState = SearchScreenUiState("", listOf() , listOf())
    }
}