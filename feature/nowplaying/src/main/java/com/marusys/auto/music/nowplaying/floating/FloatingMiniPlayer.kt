package com.marusys.auto.music.nowplaying.floating

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import com.marusys.auto.music.ui.albumart.BlurTransformation
import com.marusys.auto.music.ui.albumart.SongAlbumArtModel
import com.marusys.auto.music.ui.albumart.toSongAlbumArtModel
import com.marusys.auto.music.nowplaying.NowPlayingState
import com.marusys.auto.music.nowplaying.ui.CrossFadingAlbumArt
import com.marusys.auto.music.nowplaying.ui.ErrorPainterType


@Composable
fun FloatingMiniPlayer(
    modifier: Modifier,
    nowPlayingState: NowPlayingState,
    showExtraControls: Boolean,
    songProgressProvider: () -> Float,
    enabled: Boolean,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    if (nowPlayingState !is NowPlayingState.Playing) return
    Box(modifier, contentAlignment = Alignment.Center) {

        // show background

        SongBlurredBackground(
            modifier = Modifier.fillMaxSize(),
            songAlbumArtModel = nowPlayingState.song.toSongAlbumArtModel()
        )

        // draw content
    }
}

@Composable
fun SongBlurredBackground(
    modifier: Modifier,
    songAlbumArtModel: SongAlbumArtModel
) {
    val context = LocalContext.current
    CrossFadingAlbumArt(
        modifier = modifier,
        songAlbumArtModel = songAlbumArtModel,
        errorPainterType = ErrorPainterType.SOLID_COLOR,
        blurTransformation = remember { BlurTransformation(context = context) },
        colorFilter = ColorFilter.tint(
            Color(0xFFBBBBBB), BlendMode.Multiply
        )
    )
}