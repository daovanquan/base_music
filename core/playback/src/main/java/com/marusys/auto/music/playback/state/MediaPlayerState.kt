package com.marusys.auto.music.playback.state

import com.marusys.auto.music.model.playback.PlaybackState
import com.marusys.auto.music.store.model.song.Song

data class MediaPlayerState(
    val currentPlayingSong: Song?,
    val playbackState: PlaybackState
) {

    companion object {
        val empty = MediaPlayerState(null, PlaybackState.emptyState)
    }

}
