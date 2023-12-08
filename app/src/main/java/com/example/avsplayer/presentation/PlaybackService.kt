package com.example.avsplayer.presentation

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

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

        setSessionActivity()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_AVS_PLAYER_PLAYBACK) {
            stopForeground(STOP_FOREGROUND_DETACH)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @OptIn(UnstableApi::class)
    private fun setSessionActivity() {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
        mediaSession?.setSessionActivity(pendingIntent)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? = mediaSession

    companion object {
        val STOP_AVS_PLAYER_PLAYBACK = "STOP_AVS_PLAYER_PLAYBACK"
    }
}
