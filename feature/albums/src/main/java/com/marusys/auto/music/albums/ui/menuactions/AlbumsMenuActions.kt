package com.marusys.auto.music.albums.ui.menuactions

import com.marusys.auto.music.ui.menu.MenuActionItem
import com.marusys.auto.music.ui.menu.addToPlaylists
import com.marusys.auto.music.ui.menu.addToQueue
import com.marusys.auto.music.ui.menu.play
import com.marusys.auto.music.ui.menu.playNext
import com.marusys.auto.music.ui.menu.shuffle
import com.marusys.auto.music.ui.menu.shuffleNext


fun buildAlbumsMenuActions(
    onPlay: () -> Unit,
    addToQueue: () -> Unit,
    onPlayNext: () -> Unit,
    onShuffle: () -> Unit,
    onShuffleNext: () -> Unit,
    onAddToPlaylists: () -> Unit
): List<MenuActionItem> {
    return mutableListOf<MenuActionItem>()
        .apply {
            play(onPlay)
            addToQueue(addToQueue)
            playNext(onPlayNext)
            shuffle(onShuffle)
            shuffleNext(onShuffleNext)
            addToPlaylists(onAddToPlaylists)
        }
}