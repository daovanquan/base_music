package com.marusys.auto.music.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.marusys.auto.music.database.entities.PLAYLIST_ID_COLUMN
import com.marusys.auto.music.database.entities.playlist.PlaylistEntity
import com.marusys.auto.music.database.entities.playlist.PlaylistsSongsEntity


data class PlaylistWithSongsUri(
    @Embedded
    val playlistEntity: PlaylistEntity,

    @Relation(entity = PlaylistsSongsEntity::class, parentColumn = PLAYLIST_ID_COLUMN, entityColumn = PLAYLIST_ID_COLUMN)
    val songUris: List<PlaylistsSongsEntity>
)
