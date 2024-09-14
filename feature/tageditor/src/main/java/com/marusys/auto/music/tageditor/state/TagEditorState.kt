package com.marusys.auto.music.tageditor.state

import com.marusys.auto.music.store.model.tags.SongTags

sealed interface TagEditorState {
    data object Loading : TagEditorState
    data class Loaded(
        val tags: SongTags,
        val isSaving: Boolean = false,
        val isSaved: Boolean = false,
        val isFailed: Boolean = false,
    ) : TagEditorState
}