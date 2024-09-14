package com.marusys.auto.music.ui.songs

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.ui.R
import com.marusys.auto.music.ui.albumart.LocalEfficientThumbnailImageLoader
import com.marusys.auto.music.ui.albumart.LocalInefficientThumbnailImageLoader
import com.marusys.auto.music.ui.albumart.toSongAlbumArtModel
import com.marusys.auto.music.ui.anim.WaveformBar
import com.marusys.auto.music.ui.common.LocalUserPreferences
import com.marusys.auto.music.ui.menu.MenuActionItem
import com.marusys.auto.music.ui.menu.SongDropdownMenu
import com.marusys.auto.music.ui.millisToTime
import timber.log.Timber


@Composable
fun SongRow(
    modifier: Modifier,
    song: Song,
    menuOptions: List<MenuActionItem>? = null,
    songRowState: SongRowState,
    isCurrentPlaying: Boolean = false,
) {

    val efficientThumbnailLoading = LocalUserPreferences.current.librarySettings.cacheAlbumCoverArt
    Row(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SongInfoRow(
            modifier = Modifier.weight(1f),
            song = song,
            isCurrentPlaying = isCurrentPlaying,
            efficientThumbnailLoading = efficientThumbnailLoading
        )

    }

}

enum class SongRowState {
    MENU_SHOWN, SELECTION_STATE_NOT_SELECTED, SELECTION_STATE_SELECTED, EMPTY
}

@Composable
fun SongInfoRow(
    modifier: Modifier,
    song: Song,
    isCurrentPlaying: Boolean,
    efficientThumbnailLoading: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10.dp)),
                model = song.toSongAlbumArtModel(),
                imageLoader = if (efficientThumbnailLoading) LocalEfficientThumbnailImageLoader.current else LocalInefficientThumbnailImageLoader.current,
                contentDescription = "Cover Photo",
                contentScale = ContentScale.Crop,
                fallback = rememberVectorPainter(image = Icons.Rounded.MusicNote),
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder),
                onError = { Timber.d("uri: ${it.result.request.data}" + it.result.throwable.stackTraceToString()) }
            )
            if(isCurrentPlaying) WaveformBar(modifier = Modifier.size(50.dp), barCount = 4)
        }


        Spacer(modifier = Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {

            Text(
                modifier = Modifier.basicMarquee(),
                text = song.metadata.title,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.basicMarquee().weight(1f),
                    text = song.metadata.artistName.toString(),
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun SongOverflowMenu(menuOptions: List<MenuActionItem>) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
    }
    SongDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        actions = menuOptions
    )
}