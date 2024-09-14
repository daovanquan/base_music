package com.marusys.auto.music.nowplaying.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marusys.auto.music.store.model.song.Song

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongTextInfo(
    modifier: Modifier,
    song: Song,
    showArtist: Boolean = true,
    showAlbum: Boolean = true
) {


    Column(modifier = modifier) {

        if (showAlbum) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = song.metadata.albumName ?: "<unknown>",
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(5.dp))
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    initialDelayMillis = 1000,
                    animationMode = MarqueeAnimationMode.Immediately
                ),
            text = song.metadata.title,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            fontSize = 50.sp,
            maxLines = 1
        )

        if (showArtist) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = song.metadata.artistName ?: "<unknown>",
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                fontSize = 20.sp,
                maxLines = 1
            )
        }
    }

}

