package com.marusys.auto.music.tageditor.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.store.repository.TagsRepository
import com.marusys.auto.music.store.model.tags.SongTags
import com.marusys.auto.music.tageditor.state.TagEditorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel


class TagEditorViewModel(
    private val tagsRepository: TagsRepository,
    song: String,
) : ScopeViewModel() {

    private val _state = MutableStateFlow<TagEditorState>(TagEditorState.Loading)
    val state: StateFlow<TagEditorState> get() = _state.asStateFlow()
    private val songUri: Uri = Uri.parse(song)

    init {
        viewModelScope.launch {
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val songTags = tagsRepository.getSongTags(songUri)
        _state.value = TagEditorState.Loaded(songTags)
    }

    fun saveTags(songTags: SongTags) {
        viewModelScope.launch {
            val currentState = state.value as TagEditorState.Loaded
            _state.value = currentState.copy(isSaving = true, isSaved = false)
            try {
                tagsRepository.editTags(songUri, songTags,)
                _state.getAndUpdate {
                    if (it is TagEditorState.Loaded)
                        it.copy(isSaved = true, isSaving = false, isFailed = false)
                    else
                        TagEditorState.Loading
                }
            } catch (e: Exception) {
                _state.getAndUpdate {
                    if (it is TagEditorState.Loaded)
                        it.copy(isSaved = false, isSaving = false, isFailed = true)
                    else
                        TagEditorState.Loading
                }
            }
        }
    }

}