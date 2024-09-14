package com.marusys.auto.music.database.entities.activity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marusys.auto.music.database.entities.DURATION_SECONDS_COLUMN
import com.marusys.auto.music.database.entities.LISTENING_SESSION_TABLE
import com.marusys.auto.music.database.entities.START_TIME_COLUMN


@Entity(LISTENING_SESSION_TABLE)
data class ListeningSessionEntity(

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = START_TIME_COLUMN)
    val startTimeEpoch: Long, // convert to kotlin datetime,
    @ColumnInfo(name = DURATION_SECONDS_COLUMN)
    val durationSeconds: Int,
)