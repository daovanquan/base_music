package com.marusys.auto.music.nowplaying.timer

import androidx.lifecycle.ViewModel
import com.marusys.auto.music.playback.PlaybackManager
import org.koin.androidx.scope.ScopeViewModel

class SleepTimerViewModel constructor(
    private val playbackManager: PlaybackManager
): ScopeViewModel() {

    fun schedule(
        minutes: Int,
        finishLastSong: Boolean = false
    ) {
        playbackManager.setSleepTimer(minutes, finishLastSong)
    }

    fun deleteTimer() {
        playbackManager.deleteSleepTimer()
    }


}