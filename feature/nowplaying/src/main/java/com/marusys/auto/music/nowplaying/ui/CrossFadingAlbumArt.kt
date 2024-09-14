package com.marusys.auto.music.nowplaying.ui

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.marusys.auto.music.ui.R
import com.marusys.auto.music.ui.albumart.BlurTransformation
import com.marusys.auto.music.ui.albumart.LocalInefficientThumbnailImageLoader
import com.marusys.auto.music.ui.albumart.SongAlbumArtModel
import com.marusys.auto.music.ui.common.LocalUserPreferences
import com.marusys.auto.music.ui.model.toPlayerTheme
import com.marusys.auto.music.ui.theme.isAppInDarkTheme


enum class ErrorPainterType {
    PLACEHOLDER, SOLID_COLOR
}

@Composable
fun CrossFadingAlbumArt(
    modifier: Modifier,
    containerModifier: Modifier = Modifier,
    songAlbumArtModel: SongAlbumArtModel,
    errorPainterType: ErrorPainterType,
    colorFilter: ColorFilter? = null,
    blurTransformation: BlurTransformation? = null,
    contentScale: ContentScale = ContentScale.Crop,
    gradientCenter: Boolean = false
) {

    val context = LocalContext.current
    val imageRequest = remember(songAlbumArtModel.uri.toString()) {
        ImageRequest.Builder(context)
            .data(if(songAlbumArtModel.uri == Uri.EMPTY) R.drawable.placeholder else songAlbumArtModel)
            .apply { if (blurTransformation != null) this.transformations(blurTransformation) }
            .size(Size.ORIGINAL).build()
    }

    val backgroundColor = if(isAppInDarkTheme()) Color.Black else MaterialTheme.colorScheme.surfaceContainer

    var firstPainter by remember {
        mutableStateOf<Painter>(ColorPainter(Color.Black))
    }

    var secondPainter by remember {
        mutableStateOf<Painter>(ColorPainter(Color.Black))
    }

    var isUsingFirstPainter by remember {
        mutableStateOf(true)
    }

    val solidColorPainter = remember { ColorPainter(Color.Black) }
    val placeholderPainter = painterResource(id = R.drawable.placeholder)

    rememberAsyncImagePainter(
        model = imageRequest,
        contentScale = ContentScale.Crop,
        imageLoader = LocalInefficientThumbnailImageLoader.current,
        onState = {
            when (it) {
                is AsyncImagePainter.State.Success -> {
                    val newPainter = it.painter
                    if (isUsingFirstPainter) {
                        secondPainter = newPainter
                    } else {
                        firstPainter = newPainter
                    }
                    isUsingFirstPainter = !isUsingFirstPainter
                }

                is AsyncImagePainter.State.Error -> {
                    if (isUsingFirstPainter) {
                        secondPainter =
                            if (errorPainterType == ErrorPainterType.PLACEHOLDER) placeholderPainter
                            else solidColorPainter
                    } else {
                        firstPainter =
                            if (errorPainterType == ErrorPainterType.PLACEHOLDER) placeholderPainter
                            else solidColorPainter
                    }
                    isUsingFirstPainter = !isUsingFirstPainter
                }

                else -> {

                }
            }
        }
    )

    Box(contentAlignment = Alignment.Center) {
        Crossfade(modifier = containerModifier, targetState = isUsingFirstPainter, label = "") {
            Image(
                modifier = modifier,
                painter = if (it) firstPainter else secondPainter,
                contentDescription = null,
                colorFilter = colorFilter,
                contentScale = contentScale
            )
        }

        if(gradientCenter)
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRadialGradientOverlay(backgroundColor)
            }
    }
}

private fun DrawScope.drawRadialGradientOverlay(gradientColor: Color) {
    val radius = size.minDimension / 2f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(gradientColor.copy(alpha = 0f),  gradientColor),
            center = Offset(radius, radius),
            radius = radius + 10f
        ),
        radius = radius + 10f,
        center = Offset(radius, radius)
    )
}