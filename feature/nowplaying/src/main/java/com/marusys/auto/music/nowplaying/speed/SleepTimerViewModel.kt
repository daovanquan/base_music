package com.marusys.auto.music.nowplaying.speed

import androidx.lifecycle.ViewModel
import com.marusys.auto.music.playback.PlaybackManager
import org.koin.androidx.scope.ScopeViewModel


class PlaybackSpeedViewModel constructor(
    private val playbackManager: PlaybackManager
): ScopeViewModel() {

    val playbackParameters: Pair<Float, Float>
        get() = playbackManager.playbackParameters

    fun setParameters(speed: Float, pitch: Float) {
        playbackManager.setPlaybackParameters(speed, pitch)
    }

}