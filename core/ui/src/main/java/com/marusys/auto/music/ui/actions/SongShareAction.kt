package com.marusys.auto.music.ui.actions

import android.content.Context
import com.marusys.auto.music.store.model.song.Song


fun interface SongShareAction {

    fun share(context: Context, songs: List<Song>)

}