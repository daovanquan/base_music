package com.marusys.auto.music.store.common.extention

import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.BaseColumns
import android.provider.MediaStore

fun Context.getMediaStoreIdFromPath(path: String): Long {
    var id = 0L
    val projection = arrayOf(
        MediaStore.Audio.Media._ID
    )

    val uri = getFileUri(path)
    val selection = "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs = arrayOf(path)

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                id = cursor.getLong(MediaStore.Audio.Media._ID)
            }
        }
    } catch (ignored: Exception) {
    }

    return id
}

fun Context.getArtist(path: String): String? {
    val projection = arrayOf(
        MediaStore.Audio.Media.ARTIST
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(
            path
        )

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getString(MediaStore.Audio.Media.ARTIST)
            }
        }
    } catch (ignored: Exception) {
    }

    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
    } catch (ignored: Exception) {
        retriever.release()
        null
    } finally {
        retriever.release()
    }
}

fun Context.getDuration(path: String): Long? {
    val projection = arrayOf(
        MediaStore.MediaColumns.DURATION
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(path)

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return (cursor.getInt(MediaStore.MediaColumns.DURATION).toLong())
            }
        }
    } catch (ignored: Exception) {
    }

    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
    } catch (ignored: Exception) {
        retriever.release()
        null
    } finally {
        retriever.release()
    }
}

fun Context.getAlbum(path: String): String? {
    val projection = arrayOf(
        MediaStore.Audio.Media.ALBUM
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(
            path
        )

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getString(MediaStore.Audio.Media.ALBUM)
            }
        }
    } catch (ignored: Exception) {
    }

    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(path)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
    } catch (ignored: Exception) {
        retriever.release()
        null
    } finally {
        retriever.release()
    }
}

fun Context.getAlbumID(path: String): Long? {
    val projection = arrayOf(
        MediaStore.Audio.Media.ALBUM_ID
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(
            path
        )

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getLong(MediaStore.Audio.Media.ALBUM_ID)
            }
        }
    } catch (ignored: Exception) {
    }
    return null
}

fun Context.getSize(path: String): Long? {
    val projection = arrayOf(
        MediaStore.Audio.Media.SIZE
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(
            path
        )

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getLong(MediaStore.Audio.Media.SIZE)
            }
        }
    } catch (ignored: Exception) {
    }
    return null
}

fun Context.getTrack(path: String): Int? {
    val projection = arrayOf(
        MediaStore.Audio.Media.TRACK
    )

    val uri = getFileUri(path)
    val selection =
        if (path.startsWith("content://")) "${BaseColumns._ID} = ?" else "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs =
        if (path.startsWith("content://")) arrayOf(path.substringAfterLast("/")) else arrayOf(
            path
        )

    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getInt(MediaStore.Audio.Media.TRACK)
            }
        }
    } catch (ignored: Exception) {
    }
    return null
}

fun Context.getFileUri(path: String) = when {
    path.isAudioSlow() -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    else -> MediaStore.Files.getContentUri("external")
}
