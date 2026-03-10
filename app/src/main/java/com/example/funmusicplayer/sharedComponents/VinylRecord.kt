package com.example.funmusicplayer.sharedComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.funmusicplayer.mains.VoidBlack

@Composable
fun VinylRecord(
    isPlaying: Boolean,
    themeColor: Color,
    stampGradient: List<Color>,
    modifier: Modifier = Modifier
) {
    val motorSpeed by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 40f),
        label = "motor_speed"
    )
    val motorEngagementScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.96f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
        label = "motor_bump"
    )
    var accumulatedRotation by remember { mutableFloatStateOf(value = 0f) }

    LaunchedEffect(Unit) {
        var lastFrame = withFrameMillis { it }
        while (true) {
            val currentFrame = withFrameMillis { it }
            accumulatedRotation += ((currentFrame - lastFrame) * 0.09f) * motorSpeed
            lastFrame = currentFrame
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .scale(scale = motorEngagementScale)
            .graphicsLayer { rotationZ = accumulatedRotation }
            .shadow(
                elevation = if (isPlaying) 20.dp else 8.dp,
                shape = CircleShape,
                spotColor = Color.Black
            )
            .clip(shape = CircleShape)
            .background(color = Color(0xFF111113))
    ) {
        val center = Offset(x = size.width / 2, y = size.height / 2)
        val radius = size.width / 2

        for (i in 1..12) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = radius - (i * 10f),
                center = center,
                style = Stroke(width = 1.5f)
            )
        }

        drawRect(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    themeColor.copy(alpha = 0.4f),
                    Color.Transparent,
                    themeColor.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = center
            ),
            blendMode = BlendMode.Screen
        )

        drawCircle(
            brush = Brush.linearGradient(colors = stampGradient),
            radius = radius * 0.35f,
            center = center
        )

        drawCircle(color = VoidBlack, radius = radius * 0.06f, center = center)
    }
}
