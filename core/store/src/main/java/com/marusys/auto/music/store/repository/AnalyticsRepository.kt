package com.marusys.auto.music.store.repository

import com.marusys.auto.music.database.dao.ActivityDao
import com.marusys.auto.music.database.entities.activity.ListeningSessionEntity
import com.marusys.auto.music.model.activity.ListeningSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.util.Date

class AnalyticsRepository constructor(
    private val activityDao: ActivityDao
): KoinComponent {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun insertListeningSession(l: ListeningSession) {
        scope.launch {
            activityDao.insertListeningSession(l.toDBEntity())
        }
    }

    suspend fun getAllListeningSessions(): List<ListeningSession> {
        return activityDao.getAllListeningSessions().map { it.toModel() }
    }

    private fun ListeningSession.toDBEntity() =
        ListeningSessionEntity(0, startTime.time, durationSeconds)

    private fun ListeningSessionEntity.toModel() =
        ListeningSession(Date(startTimeEpoch), durationSeconds)

    private val Date.timeSeconds get() = time / 1000

}