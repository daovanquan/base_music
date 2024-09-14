package com.marusys.auto.music.nowplaying.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.marusys.auto.music.model.playback.PlayerState
import com.marusys.auto.music.model.playback.RepeatMode
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.ui.albumart.toSongAlbumArtModel
import com.marusys.auto.music.nowplaying.lyrics.LiveLyricsScreen
import com.marusys.auto.music.nowplaying.lyrics.fadingEdge
import com.marusys.auto.music.nowplaying.viewmodel.INowPlayingViewModel


@Composable
fun PlayingScreen(
    modifier: Modifier,
    song: Song,
    repeatMode: RepeatMode,
    isShuffleOn: Boolean,
    playbackState: PlayerState,
    screenSize: NowPlayingScreenSize,
    nowPlayingActions: INowPlayingViewModel,
) {
    AutoPlayerScreen(
        modifier,
        song,
        playbackState,
        repeatMode,
        isShuffleOn,
        nowPlayingActions,
    )
}

@Composable
fun AutoPlayerScreen(
    modifier: Modifier,
    song: Song,
    playbackState: PlayerState,
    repeatMode: RepeatMode,
    isShuffleOn: Boolean,
    nowPlayingActions: INowPlayingViewModel,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(20.dp)) {
            CrossFadingAlbumArt(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                containerModifier = Modifier,
                songAlbumArtModel = song.toSongAlbumArtModel(),
                errorPainterType = ErrorPainterType.PLACEHOLDER,
                gradientCenter = true
            )
        }
        Column(
            Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var isShowingLyrics by remember {
                mutableStateOf(false)
            }

            SongControls(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = playbackState == PlayerState.PLAYING,
                onPrevious = nowPlayingActions::previousSong,
                onTogglePlayback = nowPlayingActions::togglePlayback,
                onNext = nowPlayingActions::nextSong,
                onJumpForward = nowPlayingActions::jumpForward,
                onJumpBackward = nowPlayingActions::jumpBackward
            )

            if(!isShowingLyrics) {
                Box(Modifier.weight(1f)){}
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SongTextInfo(
                    modifier = Modifier.fillMaxWidth(),
                    song = song,
                    showAlbum = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                SongProgressInfo(
                    modifier = Modifier.fillMaxWidth(),
                    songDuration = song.metadata.durationMillis,
                    songProgressProvider = nowPlayingActions::currentSongProgress,
                    onUserSeek = nowPlayingActions::onUserSeek
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
            PlayerFooter(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                songUi = song,
                isShuffleOn = isShuffleOn,
                repeatMode = repeatMode,
                isLyricsOpen = isShowingLyrics,
                onOpenQueue = nowPlayingActions::showQueue,
                onOpenLyrics = { isShowingLyrics = !isShowingLyrics },
                onToggleRepeatMode = nowPlayingActions::toggleRepeatMode,
                onToggleShuffle = nowPlayingActions::toggleShuffleMode
            )
        }
    }
}