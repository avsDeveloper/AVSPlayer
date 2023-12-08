package com.example.avsplayer.presentation

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.avsplayer.presentation.PlaybackService.Companion.STOP_AVS_PLAYER_PLAYBACK
import com.example.avsplayer.presentation.theme.AVSPlayerTheme
import com.example.avsplayer.presentation.view.AVSPlayerView
import com.example.avsplayer.presentation.view.AVSProgressIndicatorView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity(), MediaController.Listener {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var controllerFuture : ListenableFuture<MediaController>
    private lateinit var resultReceiver : ActivityResultLauncher<Intent>
    private var uri: Uri? = null
    private var fileName: String = "" // let it be empty even if it won't be retrieved
    lateinit var player: Player

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() { viewModel.showBottomSheet() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // finish activity and session if true
        lifecycleScope.launch {
            viewModel.isFinished.collect { isFinished ->
                if (isFinished)  {
                    stopPlayback()
                    finish()
                }
            }
        }

        // here we set Compose UI. Trying to figure out how it works ))
        setContent {
            AVSPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    PlayerScreen()
                }
            }
        }

        // remove toolbar, etc. to show player in full screen mode
        setFullScreen()

        // show bottom sheet dialog instead of closing the app
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        // set "Selected" UI state if media files picked
        resultReceiver = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK){
                fileName = it.data?.toString()!!
                uri = it.data?.data
                viewModel.setSelected()
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                    || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
                        window.insetsController?.hide(WindowInsetsCompat.Type.systemBars())
                }
                view.onApplyWindowInsets(windowInsets)
            }
        }



    }

    private fun stopPlayback() {
        val stopIntent = Intent(this@MainActivity, PlaybackService::class.java)
        stopIntent.action = STOP_AVS_PLAYER_PLAYBACK
        startService(stopIntent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlayerScreen() {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        val isBottomSheetShown = viewModel.isBottomSheetShown.collectAsStateWithLifecycle()

        when (uiState.value) {

            // show player
            UIState.Ready -> {
                AVSPlayerView(
                    controllerFuture.get(),
                    isBottomSheetShown.value,
                    viewModel
                )
            }

            // create media session and show progress bar
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

                // show indicator
                AVSProgressIndicatorView()
            }

            // launch picker here =)
            else -> {
                stopPlayback()
                openPicker()
            }
        }
    }

    //  open standard Android file browser to pick audio / video file
    private fun openPicker() {
        val pickMediaIntent = Intent()
            .apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                action = Intent.ACTION_OPEN_DOCUMENT
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/*", "video/*"))
            }
        resultReceiver.launch(pickMediaIntent)
    }

    private fun createMediaItem() : MediaItem {

        val ret = MediaMetadataRetriever()
        ret.setDataSource(this,  uri)

        // let mediaItem be the title for simplicity =)

        val title = ret.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: uri?.let { it1 ->
            it1.path?.let {
                File(it).name
            }
        }

        val mediaItem = MediaItem
            .Builder()
            .setMediaId(uri.toString())
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setTitle(title)
                    .build()
            )
            .build()
        return mediaItem
    }

    override fun onStop() {
        super.onStop()
        // ToDo check what needs to be disposed here to prevent memleaks
    }
}
