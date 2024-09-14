package com.marusys.auto.music.testing.playback

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.RepeatMode
import com.marusys.auto.music.model.playback.PlaybackState

class FakeMediaController {

    var playbackState: Int = Player.STATE_IDLE

    val mediaItemCount: Int get() = mediaItems.size
    val currentMediaItemIndex: Int get() = if(currentMediaItem == null) -1 else mediaItems.indexOf(currentMediaItem)
    private val mediaItems = mutableListOf<MediaItem>()
    var currentMediaItem: MediaItem? = null
    var shuffleModeEnabled: Boolean = false

    private var listener: Player.Listener? = null

    @RepeatMode
    var repeatMode: Int =  Player.REPEAT_MODE_OFF

    fun clearMediaItems() {
        mediaItems.clear()
    }

    fun prepare() {
        // Simulate preparation logic
        playbackState = Player.STATE_READY
        listener?.onPlaybackStateChanged(playbackState)
    }

    fun sendCustomCommand(pair: Pair<String, Bundle>, bundleOf: Bundle) {
        // Simulate sending a custom command
//        listener?.onAvailableCommandsChanged(Player.Commands.Builder().add(pair.first) .build())
    }

    fun seekToNext() {
        if (shuffleModeEnabled) {
            currentMediaItem = mediaItems.randomOrNull()
        } else {
            val nextIndex = (currentMediaItemIndex + 1).takeIf { it < mediaItems.size } ?: 0
            currentMediaItem = mediaItems[nextIndex]
        }
        listener?.onMediaItemTransition(currentMediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
    }

    fun seekToPrevious() {
        if (shuffleModeEnabled) {
            currentMediaItem = mediaItems.randomOrNull()
        } else {
            val previousIndex = (currentMediaItemIndex - 1).takeIf { it >= 0 } ?: mediaItems.size - 1
            currentMediaItem = mediaItems[previousIndex]
        }
        listener?.onMediaItemTransition(currentMediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)

    }

    fun seekTo(index: Long, positionMs: Int = 0) {
        if (index in mediaItems.indices) {
            currentMediaItem = mediaItems[index.toInt()]
            currentPosition = positionMs.toLong()
        }
    }

    fun removeMediaItem(index: Int) {
        mediaItems.removeAt(index).also {
            if (currentMediaItem == it) {
                currentMediaItem = mediaItems.getOrNull(0)
            }
        }
    }

    fun moveMediaItem(from: Int, to: Int) {
        if (from in mediaItems.indices && to in mediaItems.indices) {
            val item = mediaItems.removeAt(from)
            mediaItems.add(to, item)
            if (currentMediaItem == item) {
                currentMediaItem = item
            }
        }
    }

    fun setMediaItems(mediaItems: List<MediaItem>, index: Int, startPosition: Int) {
        this.mediaItems.clear()
        this.mediaItems.addAll(mediaItems)
        currentMediaItem = mediaItems.getOrNull(index)
        currentPosition = startPosition.toLong()
        listener?.onMediaItemTransition(currentMediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
    }

    fun play() {
        playbackState = Player.STATE_READY
        playWhenReady = true
        listener?.onPlaybackStateChanged(playbackState)
    }

    fun stop() {
        playbackState = Player.STATE_IDLE
        playWhenReady = false
        listener?.onPlaybackStateChanged(playbackState)
    }

    fun addMediaItems(i: Int, toMediaItems: List<MediaItem>) {
        this.mediaItems.addAll(i, toMediaItems)
    }

    fun addMediaItems(i: List<MediaItem>) {
        this.mediaItems.addAll(i)
    }

    fun getMediaItemAt(it: Int): MediaItem? {
        return mediaItems.getOrNull(it)
    }

    fun addListener(listener: Player.Listener) {
        this.listener = listener
    }

    data class Parameter(var speed: Float, var pitch: Float)

    var playWhenReady: Boolean = false
    var playbackParameters: Parameter = Parameter(1.0f, 1f)
    var currentPosition: Long = 0
    var duration: Long = 0
}