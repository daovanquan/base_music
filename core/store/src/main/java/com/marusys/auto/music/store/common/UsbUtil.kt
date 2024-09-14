package com.marusys.auto.music.store.common

import android.content.Context
import com.marusys.auto.music.model.song.BasicSongMetadata
import com.marusys.auto.music.store.common.extention.getAlbum
import com.marusys.auto.music.store.common.extention.getAlbumID
import com.marusys.auto.music.store.common.extention.getArtist
import com.marusys.auto.music.store.common.extention.getDuration
import com.marusys.auto.music.store.common.extention.getFileUri
import com.marusys.auto.music.store.common.extention.getFilenameFromPath
import com.marusys.auto.music.store.common.extention.getMediaStoreIdFromPath
import com.marusys.auto.music.store.common.extention.getSize
import com.marusys.auto.music.store.common.extention.getTrack
import com.marusys.auto.music.store.common.extention.isAudioFast
import com.marusys.auto.music.store.model.song.Song
import timber.log.Timber
import java.io.File

fun findAudioFile(
    file: File,
    songList: ArrayList<Song>,
    context: Context
){
    val path = file.absolutePath
    if(file.isFile){
        if(path.isAudioFast()){
            Timber.i("$path is a audio ")
            val song = getSongMetaDataFromPath(context,path)
            song?.let {
                songList.add(it)
            }
        }else if(file.isDirectory){
            file.listFiles().orEmpty().forEach { child ->
                findAudioFile(
                    child,
                    songList,
                    context)
            }
        }
    }
}

private fun getSongMetaDataFromPath(context: Context, path: String): Song?{
    val title = path.getFilenameFromPath()
    val artist = context.getArtist(path)
    val duration = context.getDuration(path)
    val size = context.getSize(path)
    val album = context.getAlbum(path)
    val albumID = context.getAlbumID(path)
    val track = context.getTrack(path)
    val fileUri = context.getFileUri(path)


    val basicMetadata = BasicSongMetadata(
        title = title,
        artistName = artist ?: "<unknown>",
        albumName = album ?: "<unknown>",
        durationMillis = duration ?: 0,
        sizeBytes = size ?: 0,
        trackNumber = track?.rem(1000)
    )
    try {
        return Song(
            uri = fileUri,
            metadata = basicMetadata,
            filePath = path,
            albumId = albumID
        )
        Timber.d("Song has uri: $fileUri, metadata: $basicMetadata")
    } catch (e: Exception) {
        Timber.e(e) // ignore the song for now if any problems occurred
    }
    return null
}