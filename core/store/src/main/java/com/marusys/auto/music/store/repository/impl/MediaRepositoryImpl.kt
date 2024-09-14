package com.marusys.auto.music.store.repository.impl

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.SessionToken
import com.marusys.auto.music.model.song.BasicSongMetadata
import com.marusys.auto.music.store.common.findAudioFile
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.store.model.song.SongLibrary
import com.marusys.auto.music.store.repository.MediaRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File

/**
 * A class that is responsible for manipulating songs on the Android device.
 * It uses the MediaStore as the underlying database and exposes all the user's
 * library inside a [StateFlow] which automatically updates when the MediaStore updates.
 * Also, it provides methods to delete songs, and change their tags.
 */
open class MediaRepositoryImpl: MediaRepository() {

    private val context: Context by inject()
    private var mediaSyncJob: Job? = null
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    val result: ArrayList<Song> = arrayListOf()

    init {
        Timber.d("MediaRepositoryImpl initialized")
    }

    override var permissionListener: PermissionListener? = null

    /** A state flow that contains all the songs in the user's device
    Automatically updates when the MediaStore changes
     */
    override val songsFlow =
        userPreferencesRepository.librarySettingsFlow.map { it.usbIdConnected }.flatMapLatest { usbIdConnected ->
            callbackFlow {
            Timber.d(TAG, "Initializing callback flow to get all songs")

            var lastChangedUri: Uri? = null
            val observer = object : ContentObserver(null) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    Timber.d(TAG, "ContentObserver onChange called with uri: $uri")
                    // Sometimes Android sends duplicate callbacks when media changes for the same URI
                    // this ensures that we don't sync twice
                    if (uri == lastChangedUri) return
                    lastChangedUri = uri

                    if (mediaSyncJob?.isActive == true) {
                        // we are already syncing, no need to complicate things more
                        return
                    } else {
                        mediaSyncJob = launch {
                            try {
                                send(getAllSongs())
                            } catch (e: Exception) {
                                Timber.e(e.message)
                            } finally {
                                mediaSyncJob = null
                            }
                        }
                    }
                }
            }

            permissionListener = PermissionListener {

                mediaSyncJob = launch {
                    send(getAllSongs())
                    mediaSyncJob = null
                }

            }

            context.contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )

            // Initial Sync
            mediaSyncJob = launch {
                mediaSyncJob = launch {
                    try {
                        send(getAllSongs())
                    } catch (e: Exception) {
                        Timber.e(e.message)
                    } finally {
                        mediaSyncJob = null
                    }
                }
            }

            awaitClose {
                context.contentResolver.unregisterContentObserver(observer)
            }
        }}.map { SongLibrary(it) }
            .flowOn(Dispatchers.IO).stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = SongLibrary(listOf())
        )
    /**
     * Retrieves all the user's songs on the device along with their [BasicSongMetadata]
     */
    override suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO)  {

        if(result.isNotEmpty()){
            Timber.d("result in getAllSongs is not Empty")
            result
        }else {
            val projection =
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.TRACK
                )

            with(context) {

                val cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null, null, null, null
                ) ?: throw Exception("Invalid cursor")

                val results = mutableListOf<Song>()
                cursor.use { c ->
                    while (c.moveToNext() && isActive) {
                        val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                        val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                        val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                        val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                        val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                        val pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                        val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                        val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                        val trackNumberColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)

                        val fileUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            cursor.getInt(idColumn).toLong()
                        )

                        val basicMetadata = BasicSongMetadata(
                            title = c.getString(titleColumn),
                            artistName = c.getString(artistColumn) ?: "<unknown>",
                            albumName = c.getString(albumColumn) ?: "<unknown>",
                            durationMillis = c.getLong(durationColumn),
                            sizeBytes = c.getLong(sizeColumn),
                            trackNumber = c.getInt(trackNumberColumn) % 1000
                        )

                        try {
                            Song(
                                uri = fileUri,
                                metadata = basicMetadata,
                                filePath = c.getString(pathColumn),
                                albumId = c.getLong(albumIdColumn)
                            ).apply { Timber.d(this.toString()) }.also(results::add)
                        } catch (e: Exception) {
                            Timber.e(e) // ignore the song for now if any problems occurred
                        }
                    }
                }

                results
            }
        }
    }


    override fun scanSongListFromUsb(usbID: String): List<Song> {
        val file = File(usbID)
        findAudioFile(file, result, context)
        return result
    }

    @TargetApi(29)
    override fun deleteSong(song: Song) {

        Timber.d("Deleting song $song")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Timber.e("Attempting to delete song in R or Higher. Use Activity Contracts instead")
            return
        }

        try {
            val file = File(song.filePath)
            file.delete()
            context.contentResolver.delete(song.uri, null, null)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override suspend fun getSongPath(uri: Uri): String = withContext(Dispatchers.IO) {

        val projection =
            arrayOf(
                MediaStore.Audio.Media.DATA,
            )
        val selection = "${MediaStore.Audio.Media._ID} = ${uri.lastPathSegment!!}"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null,
            null
        ) ?: throw Exception("Invalid cursor")

        cursor.use {
            it.moveToFirst()
            val pathColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)
            return@withContext it.getString(pathColumn)
        }
    }

}