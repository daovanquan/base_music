package com.marusys.auto.music.ui.songs

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.ui.common.MultiSelectState
import com.marusys.auto.music.ui.menu.MenuActionItem


@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.selectableSongsList(
    songs: List<Song>,
    multiSelectState: MultiSelectState<Song>,
    multiSelectEnabled: Boolean,
    animateItemPlacement: Boolean = true,
    menuActionsBuilder: (Song) -> List<MenuActionItem>?,
    onSongClicked: (Song, Int) -> Unit,
    currentSongUri: Uri? = null
) {
    itemsIndexed(songs, key = { _, song -> song.uri.toString() }) { index, song ->

        val menuActions = remember {
            menuActionsBuilder(song)
        }

        val rowState = if (multiSelectEnabled && multiSelectState.selected.contains(song)) {
            SongRowState.SELECTION_STATE_SELECTED
        } else if (multiSelectEnabled) {
            SongRowState.SELECTION_STATE_NOT_SELECTED
        } else
            SongRowState.MENU_SHOWN

        SongRow(
            modifier = Modifier
                .then(if (animateItemPlacement) Modifier.animateItem(fadeInSpec = tween(), fadeOutSpec = tween()) else Modifier)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        multiSelectState.toggle(song)
                    }
                ) {
                    if (multiSelectEnabled)
                        multiSelectState.toggle(song)
                    else
                        onSongClicked(song, index)
                },
            song = song,
            menuOptions = menuActions,
            songRowState = rowState,
            isCurrentPlaying = currentSongUri == song.uri
        )

    }
}