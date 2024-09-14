package com.marusys.auto.music.ui.actions

import android.net.Uri


interface OpenTagEditorAction {
    fun open(songUri: Uri)
}