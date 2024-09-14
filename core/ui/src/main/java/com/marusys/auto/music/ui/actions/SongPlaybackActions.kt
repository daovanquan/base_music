package com.marusys.auto.music.ui.actions

import android.content.Context
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.ui.showSongsAddedToNextToast
import com.marusys.auto.music.ui.showSongsAddedToQueueToast

interface SongPlaybackActions {

    fun playNext(songs: List<Song>)
    fun addToQueue(songs: List<Song>)
    fun shuffleNext(songs: List<Song>)
    fun shuffle(songs: List<Song>)
    fun play(songs: List<Song>)

}


class SongPlaybackActionsImpl(
    private val context: Context,
    private val playbackManager: PlaybackManager
) : SongPlaybackActions {

    override fun playNext(songs: List<Song>) {
        playbackManager.playNext(songs)
        context.showSongsAddedToNextToast(songs.size)
    }

    override fun addToQueue(songs: List<Song>) {
        playbackManager.addToQueue(songs)
        context.showSongsAddedToQueueToast(songs.size)
    }

    override fun shuffleNext(songs: List<Song>) {
        playbackManager.shuffleNext(songs)
        context.showSongsAddedToNextToast(songs.size)
    }

    override fun shuffle(songs: List<Song>) {
        playbackManager.shuffle(songs)
    }

    override fun play(songs: List<Song>) {
        playbackManager.setPlaylistAndPlayAtIndex(songs)
    }
}