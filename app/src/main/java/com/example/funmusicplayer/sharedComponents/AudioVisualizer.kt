package com.example.funmusicplayer.sharedComponents

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AudioVisualizer(
    color: Color,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val infiniteTransition = rememberInfiniteTransition(
            label = "eq_transition"
        )
        
        listOf(0.8f, 0.4f, 0.9f, 0.5f).forEachIndexed { index, targetHeight ->

            val heightMultiplier by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = targetHeight,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 300 + (index*120),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar - $index"
            )

            val actualHeight by animateFloatAsState(
                targetValue = if (isPlaying) heightMultiplier else 0.15f,
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium
                ),
                label = "pause_drop_$index"
            )

            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight(fraction = actualHeight)
                    .clip(shape = CircleShape)
                    .background(color = color)
                    .shadow(elevation = 8.dp, spotColor = color)
            )
        }
    }
}
