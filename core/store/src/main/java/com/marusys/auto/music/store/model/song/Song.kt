package com.marusys.auto.music.store.model.song

import android.net.Uri
import com.marusys.auto.music.model.song.BasicSongMetadata


/**
 * Represents a song from the point of view of Android.
 * A song contains a [Uri] to identify it in the MediaStore
 * and a file path to find it on disk, album id, as well as the metadata of the song
 */
data class Song(
    val filePath: String,
    val albumId: Long?,
    val metadata: BasicSongMetadata,
    val uriNullable: Uri? = null,
) {
    constructor(uri: Uri,
                filePath: String,
                albumId: Long?,
                metadata: BasicSongMetadata,) : this(filePath, albumId, metadata, uri)
    val uri: Uri get() = uriNullable!!
    val key: String get() = if(uriNullable != null) uri.toString() else "$filePath/${albumId ?: -1}"
}
