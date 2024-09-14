package com.marusys.auto.music.store.common.extention
import android.provider.MediaStore

val audioExtensions: Array<String>
    get() = arrayOf(
        ".mp3",
        ".wav",
        ".wma",
        ".ogg",
        ".m4a",
        ".opus",
        ".flac",
        ".aac",
        ".m4b",
        ".amr"
    )

// fast extension checks, not guaranteed to be accurate
fun String.isAudioFast() = audioExtensions.any { endsWith(it, true) }

fun String.isAudioSlow() = isAudioFast() || getMimeType().startsWith("audio") || startsWith(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
)

fun String.getFilenameFromPath() = substring(lastIndexOf("/") + 1)

fun String.getFilenameExtension() = substring(lastIndexOf(".") + 1)

fun String.getMimeType(): String {
    val typesMap = HashMap<String, String>().apply {
        // audio
        put("aa", "audio/audible")
        put("aac", "audio/aac")
        put("amr", "audio/AMR")
        put("aax", "audio/vnd.audible.aax")
        put("ac3", "audio/ac3")
        put("adt", "audio/vnd.dlna.adts")
        put("adts", "audio/aac")
        put("aif", "audio/aiff")
        put("aifc", "audio/aiff")
        put("aiff", "audio/aiff")
        put("au", "audio/basic")
        put("axa", "audio/annodex")
        put("caf", "audio/x-caf")
        put("flac", "audio/flac")
        put("m3u", "audio/x-mpegurl")
        put("m3u8", "audio/x-mpegurl")
        put("m4a", "audio/m4a")
        put("m4b", "audio/m4b")
        put("m4p", "audio/m4p")
        put("m4r", "audio/x-m4r")
        put("gsm", "audio/x-gsm")
        put("cdda", "audio/aiff")
        put("mid", "audio/mid")
        put("midi", "audio/mid")
        put("mp3", "audio/mpeg")
        put("oga", "audio/ogg")
        put("ogg", "audio/ogg")
        put("wma", "audio/x-ms-wma")
        put("rmi", "audio/mid")
        put("rpm", "audio/x-pn-realaudio-plugin")
        put("sd2", "audio/x-sd2")
        put("smd", "audio/x-smd")
        put("smi", "application/octet-stream")
        put("smx", "audio/x-smd")
        put("smz", "audio/x-smd")
        put("snd", "audio/basic")
        put("spx", "audio/ogg")
        put("opus", "audio/ogg")
        put("wav", "audio/wav")
        put("wave", "audio/wav")
        put("wax", "audio/x-ms-wax")
    }

    return typesMap[getFilenameExtension().toLowerCase()] ?: ""
}
