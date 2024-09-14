package com.marusys.auto.music.network.data

import com.marusys.auto.music.network.model.NetworkErrorException
import com.marusys.auto.music.network.model.NotFoundException
import com.marusys.auto.music.network.model.SongLyricsNetwork
import com.marusys.auto.music.network.service.LyricsService
import org.koin.core.component.KoinComponent
import retrofit2.HttpException
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class LyricsSource: KoinComponent {

    val lyricsService: LyricsService by inject()

    suspend fun getSongLyrics(
        artistName: String,
        trackName: String,
        albumName: String,
        durationSeconds: Int,
    ): SongLyricsNetwork {
        return try {
            lyricsService.getSongLyrics(artistName, trackName, albumName, durationSeconds)
        } catch (e: HttpException) {
            if (e.code() == 404) throw NotFoundException("Lyrics not found")
            else throw NetworkErrorException(e.message())
        } catch (e: Exception) {
            throw NetworkErrorException(e.message ?: "Network error")
        }
    }


}