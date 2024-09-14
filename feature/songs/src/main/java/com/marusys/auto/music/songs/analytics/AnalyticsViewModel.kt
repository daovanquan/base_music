package com.marusys.auto.music.songs.analytics

import androidx.lifecycle.viewModelScope
import com.marusys.auto.music.store.repository.AnalyticsRepository
import kotlinx.coroutines.launch
import org.koin.androidx.scope.ScopeViewModel

class AnalyticsViewModel constructor(
    private val analyticsRepository: AnalyticsRepository
): ScopeViewModel() {




    init {
        viewModelScope.launch {
            updateAnalytics()
        }
    }


    private suspend fun updateAnalytics() {
        val listeningSessions = analyticsRepository.getAllListeningSessions()

    }


}