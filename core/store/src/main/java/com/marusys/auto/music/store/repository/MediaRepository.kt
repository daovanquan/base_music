package com.marusys.auto.music.store.repository

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.marusys.auto.music.model.song.BasicSongMetadata
import com.marusys.auto.music.store.repository.MediaRepository.PermissionListener
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.store.model.song.SongLibrary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
abstract class MediaRepository: KoinComponent {

    companion object {
        const val TAG = "MediaRepository"
    }

    val userPreferencesRepository: UserPreferencesRepository by inject()

    private var mediaSyncJob: Job? = null
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)


    protected open var permissionListener: PermissionListener? = null

    /** A state flow that contains all the songs in the user's device
    Automatically updates when the MediaStore changes
     */
    abstract val songsFlow: StateFlow<SongLibrary>

    /**
     * Retrieves all the user's songs on the device along with their [BasicSongMetadata]
     */
    open suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        emptyList()
    }


    @TargetApi(29)
    open fun deleteSong(song: Song) {
    }

    open suspend fun getSongPath(uri: Uri): String = withContext(Dispatchers.IO) {
        ""
    }

    /**
     * Called by the MainActivity to inform the repo that the user
     * granted the READ permission, in order to refresh the music library
     */
    open fun onPermissionAccepted() {
        permissionListener?.onPermissionGranted()
    }

    /**
     * Interface implemented inside the callback flow of the [MediaRepository]
     * to force refresh of the song library when the user grants the permission
     */
    fun interface PermissionListener {
        fun onPermissionGranted()
    }

    open fun scanSongListFromUsb(usbID: String): List<Song>{
        return emptyList()
    }
}