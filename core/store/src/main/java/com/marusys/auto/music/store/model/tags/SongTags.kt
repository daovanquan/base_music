package com.marusys.auto.music.store.model.tags

import android.graphics.Bitmap
import android.net.Uri
import com.marusys.auto.music.model.song.ExtendedSongMetadata


data class SongTags(
    val uri: Uri,
    val artwork: Bitmap? = null,
    val metadata: ExtendedSongMetadata
)