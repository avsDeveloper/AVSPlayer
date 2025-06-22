package com.avs.avsplayer.presentation.player.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import com.avs.avsplayer.R
import kotlin.math.roundToInt

@Composable
fun DraggableFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val screenDensity = LocalConfiguration.current.densityDpi / 160f
    val minOffset = (LocalConfiguration.current.screenHeightDp.coerceAtMost(
        LocalConfiguration.current.screenWidthDp
    ) - 130) * screenDensity * -1
    val maxOffset = 130 * screenDensity

    FloatingActionButton(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(minOffset, maxOffset)
                    offsetY = (offsetY + dragAmount.y).coerceIn(minOffset, maxOffset)
                }
            },
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(Icons.Filled.Menu, stringResource(R.string.floating_action_button_content_description))
    }
}