package com.marusys.auto.music.database.model

import androidx.room.Embedded
import com.marusys.auto.music.database.entities.playlist.PlaylistEntity


data class PlaylistInfoWithNumberOfSongs(
    @Embedded
    val playlistEntity: PlaylistEntity,
    val numberOfSongs: Int
)