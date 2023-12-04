package com.example.avsplayer.presentation

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackService() : MediaLibraryService() {

    private var mediaSession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer
            .Builder(this)
            .build()

        mediaSession = MediaLibrarySession
            .Builder(
                this,
                player,
                object: MediaLibrarySession.Callback {
                    override fun onAddMediaItems(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        mediaItems: MutableList<MediaItem>
                    ): ListenableFuture<MutableList<MediaItem>> {

                        val updatedMediaItems = mediaItems
                            .map { it.buildUpon().setUri(it.mediaId).build() }
                            .toMutableList()

                        return Futures.immediateFuture(updatedMediaItems)

                    }
                }).build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = mediaSession
}
