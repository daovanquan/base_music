package com.marusys.auto.music.store.model.song


class SongLibrary(
    val songs: List<Song>
) {

    /**
     * Map of song Uri to their Uris
     */
    private val songMap: Map<String, Song> = kotlin.run {
        val map = mutableMapOf<String, Song>()
        songs.forEach { song ->
            map[song.key] = song
        }
        map
    }

    private val songPathMap: Map<String, Song> = kotlin.run {
        val map = mutableMapOf<String, Song>()
        songs.forEach { song ->
            map[song.filePath] = song
        }
        map
    }

    fun getSongByUri(uri: String): Song? = songMap[uri]

    fun getSongByPath(path: String): Song? = songPathMap[path]

    fun getSongsByUris(uris: List<String>): List<Song> = kotlin.run {
        val result = mutableListOf<Song>()
        uris.forEach {
            val song = songMap[it] ?: return@forEach
            result.add(song)
        }
        result
    }

}