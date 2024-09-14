package com.marusys.auto.music.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.glance.appwidget.updateAll
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.marusys.auto.music.model.playback.PlayerState
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.ui.albumart.SongAlbumArtModel
import com.marusys.auto.music.ui.albumart.inefficientAlbumArtImageLoader
import com.marusys.auto.music.ui.albumart.toSongAlbumArtModel
import com.marusys.auto.music.widgets.ui.WidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class WidgetManager : KoinComponent {

    private val context: Context by inject()
    val playbackManager: PlaybackManager by inject()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val imageLoader = context.inefficientAlbumArtImageLoader()

    val state = playbackManager.state.map {
        updateWidgets()

        if (it.currentPlayingSong == null)
            return@map WidgetState.NoQueue

        val metadata = it.currentPlayingSong!!.metadata
        val bitmap = getSongBitmap(it.currentPlayingSong.toSongAlbumArtModel())
        val isPlaying = it.playbackState.playerState == PlayerState.PLAYING

        WidgetState.Playback(
            title = metadata.title,
            artist = metadata.artistName ?: "<unknown>",
            isPlaying = isPlaying,
            image = bitmap
        )
    }.stateIn(scope, SharingStarted.Eagerly, WidgetState.NoQueue)

    private suspend fun getSongBitmap(songAlbumArtModel: SongAlbumArtModel): Bitmap? =
        withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(songAlbumArtModel)
                .transformations(CircleCropTransformation())
                .build()

            val result = imageLoader.execute(request)
            if (result !is SuccessResult) return@withContext null

            (result.drawable as BitmapDrawable).bitmap
        }


    private fun updateWidgets() {
        scope.launch {
            CardWidget().updateAll(context)
            CircleWidget().updateAll(context)
        }
    }

}