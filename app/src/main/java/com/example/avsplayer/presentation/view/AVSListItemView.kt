package com.example.avsplayer.presentation.view

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.avsplayer.presentation.MainActivityViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.avsplayer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AVSListItemView(
    viewModel: MainActivityViewModel? = null,
    title: String,
    description: String,
    itemPos: Int,
    uri: Uri?,
    onClickCall: () -> Unit
) {
    val currentPosition = viewModel?.currentItemNum?.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (itemPos == currentPosition?.value) colorResource(id = R.color.colorPrimaryDark)
                else colorResource(id = R.color.colorBlack)
            )
            .clickable {
                onClickCall()
            }
            .padding(end = 8.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AVSMediaItemImage(
            uri = uri
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .basicMarquee(
                        animationMode = if (itemPos == currentPosition?.value) MarqueeAnimationMode.Immediately
                        else MarqueeAnimationMode.WhileFocused,
                        delayMillis = 0,
                    ),
                text = title,
                color = Color.LightGray,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = description,
                color = Color.LightGray,
                maxLines = 1,
                modifier = Modifier
                    .basicMarquee(
                        animationMode = if (itemPos == currentPosition?.value) MarqueeAnimationMode.Immediately
                        else MarqueeAnimationMode.WhileFocused,
                        delayMillis = 0,
                    ),
                style = MaterialTheme.typography.bodySmall
            )

        }


    }
}


@Composable
fun AVSMediaItemImage(
    uri: Uri?
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        launch {
            imageBitmap = loadBitmap(context.contentResolver, uri)
        }
    }

    if (imageBitmap != null) {

        Box(modifier = Modifier.padding(12.dp)) {
            Image(
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .clip(CircleShape),
            )
        }
    }
}

private suspend fun loadBitmap(
    contentResolver: ContentResolver,
    uri: Uri?
): Bitmap? {
    if (uri == null) return null

    return withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            null
        } catch (e: IOException) {
            null
        }
    }
}

