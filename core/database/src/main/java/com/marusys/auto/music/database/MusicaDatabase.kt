package com.marusys.auto.music.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marusys.auto.music.database.dao.ActivityDao
import com.marusys.auto.music.database.dao.BlacklistedFoldersDao
import com.marusys.auto.music.database.dao.LyricsDao
import com.marusys.auto.music.database.dao.PlaylistDao
import com.marusys.auto.music.database.dao.QueueDao
import com.marusys.auto.music.database.entities.activity.ListeningSessionEntity
import com.marusys.auto.music.database.entities.lyrics.LyricsEntity
import com.marusys.auto.music.database.entities.playlist.PlaylistEntity
import com.marusys.auto.music.database.entities.playlist.PlaylistsSongsEntity
import com.marusys.auto.music.database.entities.prefs.BlacklistedFolderEntity
import com.marusys.auto.music.database.entities.queue.QueueEntity


@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistsSongsEntity::class,
        BlacklistedFolderEntity::class,
        QueueEntity::class,
        ListeningSessionEntity::class,
        LyricsEntity::class
    ],
    version = 1, exportSchema = false
)
abstract class MusicaDatabase : RoomDatabase() {

    abstract fun playlistsDao(): PlaylistDao
    abstract fun blacklistDao(): BlacklistedFoldersDao
    abstract fun queueDao(): QueueDao
    abstract fun activityDao(): ActivityDao
    abstract fun lyricsDao(): LyricsDao

}