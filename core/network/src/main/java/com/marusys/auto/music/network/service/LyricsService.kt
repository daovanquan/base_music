package com.marusys.auto.music.network.service

import com.marusys.auto.music.network.model.SongLyricsNetwork
import retrofit2.http.GET
import retrofit2.http.Query


interface LyricsService {

    companion object {
        const val BASE_URL = "https://lrclib.net/"
    }

    @GET("api/get")
    suspend fun getSongLyrics(
        @Query("artist_name") artistName: String,
        @Query("track_name") trackName: String,
        @Query("album_name") albumName: String,
        @Query("duration") durationSeconds: Int,
    ): SongLyricsNetwork

}