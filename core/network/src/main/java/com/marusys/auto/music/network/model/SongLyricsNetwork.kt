package com.marusys.auto.music.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName



data class SongLyricsNetwork(
    @SerializedName("id")
    val lyricsId: Int,

    @SerializedName("plainLyrics")
    val plainLyrics: String,

    @SerializedName("syncedLyrics")
    val syncedLyrics: String
)