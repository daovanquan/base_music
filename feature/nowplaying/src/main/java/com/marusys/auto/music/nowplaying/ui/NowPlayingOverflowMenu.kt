package com.marusys.auto.music.nowplaying.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.marusys.auto.music.store.model.song.Song
import com.marusys.auto.music.ui.common.LocalCommonSongsAction
import com.marusys.auto.music.ui.menu.MenuActionItem
import com.marusys.auto.music.ui.menu.addToPlaylists
import com.marusys.auto.music.ui.menu.delete
import com.marusys.auto.music.ui.menu.equalizer
import com.marusys.auto.music.ui.menu.playbackSpeed
import com.marusys.auto.music.ui.menu.setAsRingtone
import com.marusys.auto.music.ui.menu.share
import com.marusys.auto.music.ui.menu.sleepTimer
import com.marusys.auto.music.ui.menu.songInfo
import com.marusys.auto.music.ui.menu.tagEditor
import com.marusys.auto.music.ui.showShortToast
import com.marusys.auto.music.ui.topbar.OverflowMenu
import com.marusys.auto.music.nowplaying.speed.rememberPlaybackSpeedDialog
import com.marusys.auto.music.nowplaying.timer.SleepTimerViewModel
import com.marusys.auto.music.nowplaying.timer.rememberSleepTimerDialog
import org.koin.androidx.compose.koinViewModel


interface NowPlayingOptions {
    fun addToPlaylist()
    fun sleepTimer()
    fun playbackSpeed()
    fun setAsRingtone()
    fun share()
    fun editTags()
    fun songInfo()
    fun equalizer()
    fun deleteFromDevice()
}


@Composable
fun NowPlayingOverflowMenu(
    options: NowPlayingOptions
) {

    val sleepTimerViewModel: SleepTimerViewModel = koinViewModel()

    val context = LocalContext.current
    val sleepTimerDialog = rememberSleepTimerDialog(
        onSetTimer = { minutes, finishLastSong ->
            sleepTimerViewModel.schedule(minutes, finishLastSong)
            context.showShortToast("Sleep timer set")
        },
        onDeleteTimer = {
            sleepTimerViewModel.deleteTimer()
            context.showShortToast("Sleep timer deleted")
        }
    )


    val playbackSpeedDialog = rememberPlaybackSpeedDialog(viewModel = koinViewModel())

    OverflowMenu(
        contentPaddingValues = PaddingValues(start = 16.dp, end = 36.dp, top = 4.dp, bottom = 4.dp),
        actionItems = mutableListOf<MenuActionItem>().apply {
            sleepTimer { sleepTimerDialog.launch() }
            addToPlaylists(options::addToPlaylist)
            playbackSpeed { playbackSpeedDialog.launch() }
            setAsRingtone(options::setAsRingtone)
            share(options::share)
            tagEditor(options::editTags)
            equalizer(options::equalizer)
            songInfo(options::songInfo)
            delete(options::deleteFromDevice)
        }

    )

}

@Composable
fun rememberNowPlayingOptions(
    songUi: Song
): NowPlayingOptions {

    val commonSongsActions = LocalCommonSongsAction.current
    val context = LocalContext.current

    return remember(songUi) {
        object : NowPlayingOptions {
            override fun addToPlaylist() {
                commonSongsActions.addToPlaylistDialog.launch(listOf(songUi))
            }

            override fun sleepTimer() {

            }

            override fun editTags() {
                commonSongsActions.openTagEditorAction.open(songUi.uri)
            }

            override fun playbackSpeed() {
                TODO("Not yet implemented")
            }

            override fun setAsRingtone() {
                commonSongsActions.setRingtoneAction.setRingtone(songUi.uri)
            }

            override fun share() {
                commonSongsActions.shareAction.share(context, listOf(songUi))
            }

            override fun songInfo() {
                commonSongsActions.songInfoDialog.open(songUi)
            }

            override fun equalizer() {
                commonSongsActions.openEqualizer.open()
            }

            override fun deleteFromDevice() {
                commonSongsActions.deleteAction.deleteSongs(listOf(songUi))
            }
        }
    }
}
