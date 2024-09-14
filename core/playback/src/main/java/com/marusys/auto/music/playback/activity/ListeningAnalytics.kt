package com.marusys.auto.music.playback.activity

import androidx.media3.common.Player
import com.marusys.auto.music.model.activity.ListeningSession
import com.marusys.auto.music.store.repository.AnalyticsRepository
import org.koin.core.component.KoinComponent
import java.util.Date


class ListeningAnalytics constructor(
    private val analyticsRepository: AnalyticsRepository
): Player.Listener, KoinComponent {

    private var currentListeningSessionInfo: CurrentListeningSessionInfo? = null

    private val currentTimeSeconds: Long
        get() = System.currentTimeMillis() / 1000

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (playWhenReady) {
            currentListeningSessionInfo = CurrentListeningSessionInfo(Date())
        } else {
            val l = currentListeningSessionInfo ?: return
            flushSession(l)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {

    }

    /**
     * Calculates session length and stores it in the repository
     */
    private fun flushSession(l: CurrentListeningSessionInfo) {
        val listeningSession = ListeningSession(
            l.startDate,
            (currentTimeSeconds - l.startDate.timeSeconds).toInt()
        )
        analyticsRepository.insertListeningSession(listeningSession)
    }

    private val Date.timeSeconds get() = (this.time / 1000)

    data class CurrentListeningSessionInfo(
        val startDate: Date
    )

}