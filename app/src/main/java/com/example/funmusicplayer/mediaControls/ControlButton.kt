package com.example.funmusicplayer.mediaControls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.funmusicplayer.mains.GlassSurface

enum class ControlAction { PREV, PLAY_PAUSE, NEXT }

@Composable
fun ControlButton(
    action: ControlAction,
    gradient: List<Color>,
    isPlaying: Boolean,
    buttonSize: Dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "btn_scale"
    )
    val isPrimary = action == ControlAction.PLAY_PAUSE
    val controlBrush = Brush.linearGradient(colors = gradient)

    Box(
        modifier = Modifier
            .size(size = buttonSize)
            .scale(scale = scale)
            .clip(shape = CircleShape)
            .background(color = if (isPrimary) GlassSurface else Color.Transparent)
            .border(
                width = if (isPrimary) 2.dp else 0.dp,
                brush = Brush.linearGradient(colors = gradient.map { it.copy(alpha = 0.5f) }),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size = buttonSize * 0.4f)) {
            val w = this.size.width
            val h = this.size.height

            when (action) {
                ControlAction.PLAY_PAUSE -> {
                    if (isPlaying) {
                        drawRoundRect(
                            brush = controlBrush,
                            topLeft = Offset(x = w * 0.15f, y = 0f),
                            size = Size(width = w * 0.25f, height = h),
                            cornerRadius = CornerRadius(x = 4f, y = 4f)
                        )
                        drawRoundRect(
                            brush = controlBrush,
                            topLeft = Offset(x = w * 0.6f, y = 0f),
                            size = Size(width = w * 0.25f, height = h),
                            cornerRadius = CornerRadius(x = 4f, y = 4f)
                        )
                    } else {
                        val path = Path().apply {
                            moveTo(w * 0.2f, 0f); lineTo(
                            w * 0.9f,
                            h / 2
                        ); lineTo(w * 0.2f, h); close()
                        }
                        drawPath(path = path, brush = controlBrush)
                    }
                }

                ControlAction.NEXT -> {
                    val path = Path().apply {
                        moveTo(w * 0.1f, 0f); lineTo(
                        w * 0.7f,
                        h / 2
                    ); lineTo(w * 0.1f, h); close()
                    }
                    drawPath(path = path, brush = controlBrush)
                    drawRoundRect(
                        brush = controlBrush,
                        topLeft = Offset(x = w * 0.75f, y = 0f),
                        size = Size(width = w * 0.15f, height = h),
                        cornerRadius = CornerRadius(x = 2f, y = 2f)
                    )
                }

                ControlAction.PREV -> {
                    drawRoundRect(
                        brush = controlBrush,
                        topLeft = Offset(x = w * 0.1f, y = 0f),
                        size = Size(width = w * 0.15f, height = h),
                        cornerRadius = CornerRadius(x = 2f, y = 2f)
                    )
                    val path = Path().apply {
                        moveTo(w * 0.9f, 0f); lineTo(
                        w * 0.3f,
                        h / 2
                    ); lineTo(w * 0.9f, h); close()
                    }
                    drawPath(path = path, brush = controlBrush)
                }
            }
        }
    }
}
