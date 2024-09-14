package com.marusys.auto.music.database.entities.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marusys.auto.music.database.entities.PLAYLIST_ENTITY
import com.marusys.auto.music.database.entities.PLAYLIST_ID_COLUMN
import com.marusys.auto.music.database.entities.PLAYLIST_NAME_COLUMN


@Entity(tableName = PLAYLIST_ENTITY)
data class PlaylistEntity(
    @ColumnInfo(name = PLAYLIST_ID_COLUMN)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = PLAYLIST_NAME_COLUMN)
    val name: String,
)
