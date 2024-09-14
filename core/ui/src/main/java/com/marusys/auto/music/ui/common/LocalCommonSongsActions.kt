package com.marusys.auto.music.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.marusys.auto.music.playback.PlaybackManager
import com.marusys.auto.music.store.repository.MediaRepository
import com.marusys.auto.music.ui.actions.EqualizerOpener
import com.marusys.auto.music.ui.actions.OpenTagEditorAction
import com.marusys.auto.music.ui.actions.SetRingtone
import com.marusys.auto.music.ui.actions.SetRingtoneAction
import com.marusys.auto.music.ui.actions.SongDeleteAction
import com.marusys.auto.music.ui.actions.SongPlaybackActions
import com.marusys.auto.music.ui.actions.SongPlaybackActionsImpl
import com.marusys.auto.music.ui.actions.SongShareAction
import com.marusys.auto.music.ui.actions.SongsSharer
import com.marusys.auto.music.ui.actions.rememberCreatePlaylistShortcutDialog
import com.marusys.auto.music.ui.actions.rememberSongDeleter
import com.marusys.auto.music.ui.playlist.AddToPlaylistDialog
import com.marusys.auto.music.ui.playlist.rememberAddToPlaylistDialog
import com.marusys.auto.music.ui.shortcut.ShortcutDialog
import com.marusys.auto.music.ui.songs.SongInfoDialog
import com.marusys.auto.music.ui.songs.rememberSongDialog


data class CommonSongsActions(
    val playbackActions: SongPlaybackActions,
    val shareAction: SongShareAction,
    val deleteAction: SongDeleteAction,
    val songInfoDialog: SongInfoDialog,
    val addToPlaylistDialog: AddToPlaylistDialog,
    val openEqualizer: EqualizerOpener,
    val setRingtoneAction: SetRingtoneAction,
    val openTagEditorAction: OpenTagEditorAction,
    val createShortcutDialog: ShortcutDialog
)

val LocalCommonSongsAction = staticCompositionLocalOf<CommonSongsActions>
{ throw IllegalArgumentException("not implemented") }

@Composable
fun rememberCommonSongsActions(
    playbackManager: PlaybackManager,
    mediaRepository: MediaRepository,
    openTagEditorAction: OpenTagEditorAction
): CommonSongsActions {

    val context = LocalContext.current
    val songPlaybackActions = SongPlaybackActionsImpl(context, playbackManager)
    val shareAction = SongsSharer
    val deleteAction = rememberSongDeleter(mediaRepository = mediaRepository)
    val songInfoDialog = rememberSongDialog()
    val addToPlaylistDialog = rememberAddToPlaylistDialog()
    val openEqualizer = remember { EqualizerOpener(context as Activity) }
    val setRingtoneAction = remember { SetRingtone(context) }
    val shortcutDialog = rememberCreatePlaylistShortcutDialog()

    return remember {
        CommonSongsActions(
            songPlaybackActions,
            shareAction,
            deleteAction,
            songInfoDialog,
            addToPlaylistDialog,
            openEqualizer,
            setRingtoneAction,
            openTagEditorAction,
            shortcutDialog
        )
    }
}