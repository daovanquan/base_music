package com.marusys.auto.music.nowplaying.lyrics

import com.marusys.auto.music.model.lyrics.LyricsFetchSource
import com.marusys.auto.music.model.lyrics.PlainLyrics
import com.marusys.auto.music.model.lyrics.SynchronizedLyrics


sealed interface LyricsScreenState {

    data object Loading: LyricsScreenState

    data object NotPlaying: LyricsScreenState

    data object SearchingLyrics: LyricsScreenState

    data class TextLyrics(
        val plainLyrics: PlainLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsScreenState

    data class SyncedLyrics(
        val syncedLyrics: SynchronizedLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsScreenState

    data class NoLyrics(val reason: NoLyricsReason): LyricsScreenState
}

enum class NoLyricsReason {
    NETWORK_ERROR, NOT_FOUND
}