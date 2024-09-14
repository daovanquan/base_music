package com.marusys.auto.music.albums.ui.menuactions

import com.marusys.auto.music.ui.menu.MenuActionItem
import com.marusys.auto.music.ui.menu.addToPlaylists
import com.marusys.auto.music.ui.menu.addToQueue
import com.marusys.auto.music.ui.menu.playNext
import com.marusys.auto.music.ui.menu.shuffleNext


fun buildSingleAlbumMenuActions(
    onPlayNext: () -> Unit,
    addToQueue: () -> Unit,
    onShuffleNext: () -> Unit,
    onAddToPlaylists: () -> Unit
): List<MenuActionItem> {
    return mutableListOf<MenuActionItem>()
        .apply {
            playNext(onPlayNext)
            addToQueue(addToQueue)
            shuffleNext(onShuffleNext)
            addToPlaylists(onAddToPlaylists)
        }
}