package com.example.avsplayer.presentation

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionToken
import com.example.avsplayer.presentation.theme.AVSPlayerTheme
import com.example.avsplayer.presentation.view.AVSPlayerView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), MediaController.Listener {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var controllerFuture : ListenableFuture<MediaController>
    private var uri: Uri? = null
    lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AVSPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen()
                }
            }
        }

        val resultReceiver = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                uri = it.data?.data
                viewModel.setSelected()
            }
        }

        val pickMediaIntent = Intent()
            .apply {
                type = "*/*"
                action = Intent.ACTION_OPEN_DOCUMENT
            }

        resultReceiver.launch(pickMediaIntent)
    }

    @Composable
    fun PlayerScreen() {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()

        when (uiState.value) {

            UIState.Ready -> {
                AVSPlayerView(
                    controllerFuture.get()
                )
            }

            UIState.Selected -> {
                val sessionToken = SessionToken(
                    this,
                    ComponentName(this, PlaybackService::class.java)
                )

                controllerFuture = MediaController
                    .Builder(this, sessionToken)
                    .buildAsync()

                controllerFuture.addListener(
                    {
                        viewModel.setReady()

                        uri?.let {

                            val mediaItem = createMediaItem()
                            player = controllerFuture.get()
                            player.setMediaItem(mediaItem)
                            player.prepare()
                            player.play()
                        }

                    },
                    MoreExecutors.directExecutor()
                )
            }

            else -> {
                Text(text = "asdfdaderb")
            }
        }
    }


    private fun createMediaItem() : MediaItem {

        val ret = MediaMetadataRetriever()
        ret.setDataSource(this,  uri)

        return MediaItem
            .Builder()
            .setMediaId(uri.toString())
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setArtist(ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
                    .setAlbumTitle(ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM))
                    .setTitle(ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
                    .build()
            )
            .build()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
