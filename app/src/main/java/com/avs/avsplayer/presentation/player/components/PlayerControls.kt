package com.avs.avsplayer.presentation.player.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.avs.avsplayer.R
import com.avs.avsplayer.ui.AVSPlayerTheme
import com.avs.avsplayer.ui.AccentAmber
import com.avs.avsplayer.ui.Cream
import com.avs.avsplayer.ui.PlayerBlack

@Composable
fun PlayerTopBar(
    title: String,
    subtitle: String,
    playing: Boolean,
    onOpenPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EqBars(
            playing = playing,
            modifier = Modifier.padding(end = 14.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = .92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = .38f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color.White.copy(alpha = .1f), RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenPlaylist),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_playlist),
                contentDescription = stringResource(R.string.open_playlist),
                tint = Color.White.copy(alpha = .75f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun PlayerTransport(
    playing: Boolean,
    onPrevious: () -> Unit,
    onReplay: () -> Unit,
    onPlayPause: () -> Unit,
    onForward: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransportButton(R.drawable.ic_prev, R.string.previous_item, 56.dp, 26.dp, onPrevious)
        SeekButton(R.drawable.ic_replay, R.string.rewind, "5", onReplay)
        Box(
            modifier = Modifier
                .size(84.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(Cream)
                .clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = stringResource(
                    if (playing) R.string.pause else R.string.play
                ),
                tint = PlayerBlack,
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = if (playing) 0.dp else 2.dp)
            )
        }
        SeekButton(R.drawable.ic_forward, R.string.forward, "15", onForward)
        TransportButton(R.drawable.ic_next, R.string.next_item, 56.dp, 26.dp, onNext)
    }
}

@Composable
private fun TransportButton(
    iconRes: Int,
    contentDescriptionRes: Int,
    buttonSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescriptionRes),
            tint = Color.White.copy(alpha = .7f),
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun SeekButton(
    iconRes: Int,
    contentDescriptionRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescriptionRes),
            tint = Color.White.copy(alpha = .7f),
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = .7f),
            modifier = Modifier.clearAndSetSemantics {}
        )
    }
}

@Composable
fun PlayerTimeline(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val playedFraction = if (durationMs > 0) {
        (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    } else 0f
    val fraction = dragFraction ?: playedFraction

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .pointerInput(durationMs) {
                    detectTapGestures { offset ->
                        if (durationMs > 0) {
                            onSeek((offset.x / size.width * durationMs).toLong())
                        }
                    }
                }
                .pointerInput(durationMs) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            dragFraction = (offset.x / size.width).coerceIn(0f, 1f)
                        },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            dragFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            dragFraction?.let {
                                if (durationMs > 0) onSeek((it * durationMs).toLong())
                            }
                            dragFraction = null
                        },
                        onDragCancel = { dragFraction = null }
                    )
                }
        ) {
            val trackHeight = 3.dp.toPx()
            val trackTop = (size.height - trackHeight) / 2
            val thumbX = fraction * size.width
            drawRoundRect(
                color = Color.White.copy(alpha = .12f),
                topLeft = Offset(0f, trackTop),
                size = Size(size.width, trackHeight),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
            drawRoundRect(
                color = AccentAmber,
                topLeft = Offset(0f, trackTop),
                size = Size(thumbX, trackHeight),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
            drawCircle(
                color = AccentAmber.copy(alpha = .18f),
                radius = 12.dp.toPx() / 2 + 5.dp.toPx(),
                center = Offset(thumbX, size.height / 2)
            )
            drawCircle(
                color = AccentAmber,
                radius = 7.dp.toPx(),
                center = Offset(thumbX, size.height / 2)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTime(if (dragFraction != null) (fraction * durationMs).toLong() else positionMs),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = .9f)
            )
            Text(
                text = " / " + formatTime(durationMs),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = .32f)
            )
        }
    }
}

@Composable
fun EqBars(
    playing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = AccentAmber
) {
    val maxHeights = listOf(8.dp, 15.dp, 11.dp)
    val transition = rememberInfiniteTransition(label = "eq")
    val scales = listOf(520, 700, 610).map { duration ->
        if (playing) {
            transition.animateFloat(
                initialValue = .45f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "eqBar"
            )
        } else {
            remember { mutableFloatStateOf(.6f) }
        }
    }

    Row(
        modifier = modifier.height(16.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        maxHeights.forEachIndexed { index, maxHeight ->
            val height = maxHeight * scales[index].value
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

@Preview(name = "Top bar", showBackground = true, backgroundColor = 0xFF0A0A0B)
@Composable
private fun PlayerTopBarPreview() {
    AVSPlayerTheme {
        PlayerTopBar(
            title = "A Very Long Media File Title That Should Not Touch The Button",
            subtitle = "MP3 · 320KBPS",
            playing = true,
            onOpenPlaylist = {}
        )
    }
}

@Preview(name = "Transport", showBackground = true, backgroundColor = 0xFF0A0A0B)
@Composable
private fun PlayerTransportPreview() {
    AVSPlayerTheme {
        PlayerTransport(
            playing = true,
            onPrevious = {},
            onReplay = {},
            onPlayPause = {},
            onForward = {},
            onNext = {}
        )
    }
}

@Preview(name = "Timeline", showBackground = true, backgroundColor = 0xFF0A0A0B)
@Composable
private fun PlayerTimelinePreview() {
    AVSPlayerTheme {
        PlayerTimeline(
            positionMs = 45_000L,
            durationMs = 180_000L,
            onSeek = {}
        )
    }
}

@Preview(name = "Eq bars", showBackground = true, backgroundColor = 0xFF0A0A0B)
@Composable
private fun EqBarsPreview() {
    AVSPlayerTheme {
        EqBars(playing = true)
    }
}
