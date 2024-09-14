package com.marusys.auto.music.songs.analytics



sealed interface AnalyticsScreenState {
    data object Loading: AnalyticsScreenState
    data class Loaded(
        val averageListeningTimePerDay: Int
    ): AnalyticsScreenState
}