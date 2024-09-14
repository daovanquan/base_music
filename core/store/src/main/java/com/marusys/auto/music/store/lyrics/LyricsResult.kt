package com.marusys.auto.music.store.lyrics

import com.marusys.auto.music.model.lyrics.LyricsFetchSource
import com.marusys.auto.music.model.lyrics.PlainLyrics
import com.marusys.auto.music.model.lyrics.SynchronizedLyrics


sealed interface LyricsResult {

    data object NotFound: LyricsResult

    data object NetworkError: LyricsResult

    data class FoundPlainLyrics(
        val plainLyrics: PlainLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsResult

    data class FoundSyncedLyrics(
        val syncedLyrics: SynchronizedLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsResult
}
