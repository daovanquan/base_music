package com.marusys.auto.music.nowplaying

import androidx.compose.runtime.Immutable
import com.marusys.auto.music.model.playback.PlayerState
import com.marusys.auto.music.model.playback.RepeatMode
import com.marusys.auto.music.store.model.song.Song


@Immutable
sealed interface NowPlayingState {


    @Immutable
    data object NotPlaying : NowPlayingState

    @Immutable
    data class Playing(
        val song: Song,
        val playbackState: PlayerState,
        val repeatMode: RepeatMode,
        val isShuffleOn: Boolean,
    ) : NowPlayingState

}

