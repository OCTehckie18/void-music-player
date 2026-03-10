package com.example.funmusicplayer.mediaControls

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funmusicplayer.mains.Track
import com.example.funmusicplayer.themeConstants.formatTimeMs
import kotlinx.coroutines.delay

@Composable
fun PlayerControls(
    track: Track,
    isPlaying: Boolean,
    isVisible: Boolean,
    currentTimeMs: Long,
    onTogglePlay: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentTimeMs.toFloat() / track.durationMs.toFloat()).coerceIn(0f, 1f)
    var showBar by remember { mutableStateOf(false) }
    var showPrev by remember { mutableStateOf(false) }
    var showPlay by remember { mutableStateOf(false) }
    var showNext by remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            showBar = true; delay(60)
            showPrev = true; delay(60)
            showPlay = true; delay(60)
            showNext = true
        } else {
            showBar = false; showPrev = false; showPlay = false; showNext = false
        }
    }

    val playfulSpring = spring<Float>(dampingRatio = 0.45f, stiffness = 250f)
    val fastCollapse = tween<Float>(durationMillis = 150, easing = FastOutLinearInEasing)

    val barAlpha by animateFloatAsState(
        if (showBar) 1f else 0f,
        tween(200),
        label = "bar_alpha"
    )
    val prevScale by animateFloatAsState(
        if (showPrev) 1f else 0f,
        if (showPrev) playfulSpring else fastCollapse,
        label = "prev_scale"
    )
    val playScale by animateFloatAsState(
        if (showPlay) 1f else 0f,
        if (showPlay) playfulSpring else fastCollapse,
        label = "play_scale"
    )
    val nextScale by animateFloatAsState(
        if (showNext) 1f else 0f,
        if (showNext) playfulSpring else fastCollapse,
        label = "next_scale"
    )

    val accentColor1 by animateColorAsState(
        track.accentGradient[0],
        tween(800),
        label = "acc_color1"
    )
    val accentColor2 by animateColorAsState(
        track.accentGradient[1],
        tween(800),
        label = "acc_color2"
    )
    val animatedAccentGradient = listOf(accentColor1, accentColor2)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = barAlpha }
        ) {
            Text(
                text = formatTimeMs(currentTimeMs),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.width(16.dp))

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
            ) {
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.1f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        2.dp.toPx()
                    )
                )
                drawRoundRect(
                    brush = Brush.horizontalGradient(colors = animatedAccentGradient),
                    size = Size(width = size.width * progress, height = size.height),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = Offset(x = size.width * progress, y = size.height / 2),
                    blendMode = BlendMode.Screen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "-" + formatTimeMs(track.durationMs - currentTimeMs),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.graphicsLayer {
                scaleX = prevScale; scaleY = prevScale; alpha = prevScale
            }) {
                ControlButton(
                    action = ControlAction.PREV,
                    gradient = animatedAccentGradient,
                    isPlaying = isPlaying,
                    buttonSize = 56.dp,
                    onClick = onPrev
                )
            }
            Box(modifier = Modifier.graphicsLayer {
                scaleX = playScale; scaleY = playScale; alpha = playScale
            }) {
                ControlButton(
                    action = ControlAction.PLAY_PAUSE,
                    gradient = animatedAccentGradient,
                    isPlaying = isPlaying,
                    buttonSize = 80.dp,
                    onClick = onTogglePlay
                )
            }
            Box(modifier = Modifier.graphicsLayer {
                scaleX = nextScale; scaleY = nextScale; alpha = nextScale
            }) {
                ControlButton(
                    action = ControlAction.NEXT,
                    gradient = animatedAccentGradient,
                    isPlaying = isPlaying,
                    buttonSize = 56.dp,
                    onClick = onNext
                )
            }
        }
    }
}
