package com.marusys.auto.music.store.repository

import android.net.Uri
import androidx.core.net.toUri
import com.marusys.auto.music.database.dao.QueueDao
import com.marusys.auto.music.database.entities.queue.QueueEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class QueueRepository: KoinComponent{

    private val queueDao: QueueDao by inject()

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getQueue(usbId: String?): List<DBQueueItem> =
        queueDao.getQueue(usbId)
            .map { it.toDBQueueItem() }

    fun observeQueueUris(usbId: String?): Flow<List<String>> =
        queueDao.getQueueFlow(usbId)
            .map { it.map { queueItem -> queueItem.songUri } }

    fun saveQueueFromDBQueueItems(songs: List<DBQueueItem>, usbId: String?) {
        scope.launch {
            queueDao.changeQueue(songs.map { it.toQueueEntity() }, usbId)
        }
    }

    private fun DBQueueItem.toQueueEntity() =
        QueueEntity(
            0,
            songUri.toString(),
            title,
            artist,
            album,
            filePath,
            usbId
        )

    private fun QueueEntity.toDBQueueItem(): DBQueueItem {
        return DBQueueItem(
            songUri = songUri.toUri(),
            title = title,
            artist = artist.orEmpty(),
            album = albumTitle.orEmpty(),
            filePath = filePath.orEmpty(),
            usbId = usbId
        )
    }
}

data class DBQueueItem(
    val songUri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val filePath: String,
    val usbId: String? = null
)