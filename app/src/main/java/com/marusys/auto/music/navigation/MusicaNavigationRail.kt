package com.marusys.auto.music.navigation

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.marusys.auto.music.nowplaying.NowPlayingState
import com.marusys.auto.music.nowplaying.ui.CrossFadingAlbumArt
import com.marusys.auto.music.nowplaying.ui.ErrorPainterType
import com.marusys.auto.music.ui.albumart.BlurTransformation
import com.marusys.auto.music.ui.albumart.SongAlbumArtModel
import com.marusys.auto.music.ui.albumart.toSongAlbumArtModel

@Composable
fun MusicaNavigationRail(
    modifier: Modifier,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {

    NavigationRail(
        modifier = modifier
    ) {
        topLevelDestinations.forEach { item ->
            val isSelected = currentDestination.isTopLevelDestinationInHierarchy(item)
            NavRailItem(item = item, isSelected = isSelected) {
                onDestinationSelected(item)
            }
        }
    }

}

@Composable
fun NavRailItem(
    item: TopLevelDestination,
    isSelected: Boolean,
    onDestinationSelected: () -> Unit
) {

    val icon = if (isSelected) item.iconSelected else item.iconNotSelected
    NavigationRailItem(
        selected = isSelected,
        onClick = onDestinationSelected,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = item.title) },
        alwaysShowLabel = false
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    modifier: Modifier,
    topLevelDestinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onSearchClicked: () -> Unit,
    nowPlayingState: NowPlayingState,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(canScroll = { false })

    if (nowPlayingState is NowPlayingState.Playing) {
        BlurredBackground(modifier = Modifier.fillMaxWidth().clip(
            RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp)
        ), songAlbumArtModel = nowPlayingState.song.toSongAlbumArtModel())
    } else {
        BlurredBackground(modifier = Modifier.fillMaxWidth().clip(
                RoundedCornerShape(topEnd = 40.dp, bottomEnd = 40.dp)
                ), songAlbumArtModel = SongAlbumArtModel(null, Uri.EMPTY))

    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text(text = "Music app", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onSearchClicked) {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                    }
                },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.Center,  modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start) {
            topLevelDestinations.forEach { item ->
                MainMenuItem(
                    item = item,
                    isSelected = currentDestination.isTopLevelDestinationInHierarchy(item),
                    onDestinationSelected = { onDestinationSelected(item) }
                )
            }
        }
    }
}

@Composable
fun MainMenuItem(
    item: TopLevelDestination,
    isSelected: Boolean,
    onDestinationSelected: () -> Unit
) {
    val icon = if (isSelected) item.iconSelected else item.iconNotSelected
    Row(modifier = Modifier.clickable(onClick = onDestinationSelected).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = item.title, style = MaterialTheme.typography.titleLarge)
    }

}


@Composable
fun BlurredBackground(
    modifier: Modifier,
    songAlbumArtModel: SongAlbumArtModel
) {
    val context = LocalContext.current
    CrossFadingAlbumArt(
        modifier = modifier,
        songAlbumArtModel = songAlbumArtModel,
        errorPainterType = ErrorPainterType.PLACEHOLDER,
        blurTransformation = remember { BlurTransformation(radius = 50, context = context) },
        colorFilter = ColorFilter.tint(
            Color(0xFFBBBBBB), BlendMode.Multiply
        )
    )
}

