package com.avs.avsplayer.presentation.player.components

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.avs.avsplayer.ui.AccentAmber
import com.avs.avsplayer.ui.PlayerBlack

@Composable
fun AudioOnlyPlaceholder(playing: Boolean, modifier: Modifier = Modifier) {
    val panelShape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlayerBlack)
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clip(panelShape)
            .border(5.dp, Color.White.copy(alpha = .003f), panelShape)
            .drawBehind { drawHatch() },
        contentAlignment = Alignment.Center
    ) {
        AudioMark(playing = playing)
    }
}

@Composable
private fun AudioMark(playing: Boolean) {
    val plateShape = RoundedCornerShape(38.dp)
    Box(
        modifier = Modifier
            .size(176.dp)
            .clip(RoundedCornerShape(60.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(plateShape)
                .border(5.dp, Color.White.copy(alpha = .01f), plateShape),
            contentAlignment = Alignment.Center
        ) {
            DancingMusicMark(
                playing = playing,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}

@Composable
private fun DancingMusicMark(
    playing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = AccentAmber
) {
    val transition = rememberInfiniteTransition(label = "musicMark")
    val bar1 = transition.danceScale(playing, 640)
    val bar2 = transition.danceScale(playing, 900)
    val bar3 = transition.danceScale(playing, 520)
    val bar4 = transition.danceScale(playing, 760)

    Canvas(modifier = modifier) {
        val s = size.minDimension / 24f
        val strokeWidthPx = 1.8f * s
        val cy = size.height / 2f
        drawDanceBar(cx = 5.1f * s, cy = cy, halfHeight = 2.6f * s * bar1.value, strokeWidth = strokeWidthPx, color = color)
        drawDanceBar(cx = 9.7f * s, cy = cy, halfHeight = 7.1f * s * bar2.value, strokeWidth = strokeWidthPx, color = color)
        drawDanceBar(cx = 14.3f * s, cy = cy, halfHeight = 4.6f * s * bar3.value, strokeWidth = strokeWidthPx, color = color)
        drawDanceBar(cx = 18.9f * s, cy = cy, halfHeight = 1.85f * s * bar4.value, strokeWidth = strokeWidthPx, color = color)
    }
}

@Composable
private fun InfiniteTransition.danceScale(playing: Boolean, durationMs: Int): State<Float> =
    if (playing) {
        animateFloat(
            initialValue = .55f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMs),
                repeatMode = RepeatMode.Reverse
            ),
            label = "danceBar"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

private fun DrawScope.drawDanceBar(
    cx: Float,
    cy: Float,
    halfHeight: Float,
    strokeWidth: Float,
    color: Color
) {
    drawLine(
        color = color,
        start = Offset(cx, cy - halfHeight),
        end = Offset(cx, cy + halfHeight),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawHatch() {
    val band = 8.dp.toPx()
    val stepX = 16.dp.toPx() * 1.4142f
    val diagonal = size.height
    val color = Color.White.copy(alpha = .003f)
    var x = -diagonal
    while (x < size.width + diagonal) {
        drawLine(
            color = color,
            start = Offset(x, size.height),
            end = Offset(x + diagonal, 0f),
            strokeWidth = band
        )
        x += stepX
    }
}

@Preview(name = "Audio only - playing", widthDp = 372, heightDp = 692, showBackground = true)
@Composable
private fun AudioOnlyPlaceholderPlayingPreview() {
    AVSPlayerTheme {
        AudioOnlyPlaceholder(playing = true)
    }
}

@Preview(name = "Audio only - paused", widthDp = 372, heightDp = 692, showBackground = true)
@Composable
private fun AudioOnlyPlaceholderPausedPreview() {
    AVSPlayerTheme {
        AudioOnlyPlaceholder(playing = false)
    }
}
