package com.marusys.auto.music.database.entities.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.marusys.auto.music.database.entities.PLAYLIST_ID_COLUMN
import com.marusys.auto.music.database.entities.PLAYLIST_SONG_ENTITY
import com.marusys.auto.music.database.entities.SONG_URI_STRING_COLUMN


@Entity(tableName = PLAYLIST_SONG_ENTITY, primaryKeys = [PLAYLIST_ID_COLUMN, SONG_URI_STRING_COLUMN])
data class PlaylistsSongsEntity(
    @ColumnInfo(name = PLAYLIST_ID_COLUMN)
    val playlistId: Int,

    @ColumnInfo(name = SONG_URI_STRING_COLUMN)
    val songUriString: String
)