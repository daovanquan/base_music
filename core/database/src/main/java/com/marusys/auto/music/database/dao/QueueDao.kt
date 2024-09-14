package com.marusys.auto.music.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.marusys.auto.music.database.entities.queue.QUEUE_TABLE
import com.marusys.auto.music.database.entities.queue.QueueEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface QueueDao {

    @Transaction
    suspend fun changeQueue(queue: List<QueueEntity>, usbId: String?) {
        deleteQueue(usbId)
        insertQueue(queue)
    }

    @Insert
    suspend fun insertQueue(queue: List<QueueEntity>)

    @Query("SELECT * FROM $QUEUE_TABLE WHERE usbId = :usbId")
    fun getQueueFlow(usbId: String?): Flow<List<QueueEntity>>

    @Query("DELETE FROM $QUEUE_TABLE WHERE usbId = :usbId")
    suspend fun deleteQueue(usbId: String?)

    @Query("SELECT * FROM $QUEUE_TABLE WHERE usbId = :usbId")
    suspend fun getQueue(usbId: String?): List<QueueEntity>

}