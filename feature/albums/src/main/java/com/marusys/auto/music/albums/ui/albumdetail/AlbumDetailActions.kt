package com.marusys.auto.music.albums.ui.albumdetail

interface AlbumDetailActions {
    fun play()
    fun playAtIndex(index: Int)
    fun playNext()
    fun shuffle()
    fun shuffleNext()
    fun addToQueue()
}