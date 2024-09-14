package com.marusys.auto.music.playback.volume


interface AudioVolumeChangeListener {
    fun onVolumeChanged(level: Int)
}