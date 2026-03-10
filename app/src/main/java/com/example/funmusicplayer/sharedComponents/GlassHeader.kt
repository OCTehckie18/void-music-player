package com.example.funmusicplayer.sharedComponents

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funmusicplayer.mains.GlassSurface

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GlassHeader(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    themeColor: Color,
    onAvatarClick: () -> Unit,
    onAddMusicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        Row(
            modifier = modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                themeColor = themeColor,
                showDot = false,
                modifier = Modifier
                    .sharedElement(

                        // code manipulation

                        state = rememberSharedContentState(key = "profile_avatar"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            spring(
                                dampingRatio = 0.7f,
                                stiffness = 120f
                            )
                        }
                    )
                    .size(size = 48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAvatarClick
                    )
            )

            Spacer(modifier = Modifier.width(width = 16.dp))

            Row(
                modifier = Modifier
                    .weight(weight = 1f)
                    .height(height = 48.dp)
                    .clip(shape = RoundedCornerShape(size = 24.dp))
                    .background(color = GlassSurface)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .clickable{onAddMusicClick()}
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(size = 16.dp)) {
                    val w = size.width
                    val h = size.height

                    drawLine(
                        color = Color.White.copy(alpha = 0.6f),
                        start = Offset(x=w/2, y=0f),
                        end = Offset(x=w/2, y=h),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.6f),
                        start = Offset(x=-0f, y=h/2),
                        end = Offset(x=w, y=h/2),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                Spacer(modifier = Modifier.width(width = 12.dp))

                Text(
                    text = "Add Custom Tracks..",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(width = 16.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GlassSurface)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.05f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ){
                Canvas(Modifier.size(20.dp)) {
                    val w = size.width
                    val h = size.height
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(x = 0f, y = h * 0.25f),
                        end = Offset(x = w, y = h * 0.25f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(x = w * 0.3f, y = h * 0.25f)
                    )

                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(x = 0f, y = h * 0.75f),
                        end = Offset(x = w, y = h * 0.75f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(x = w * 0.7f, y = h * 0.75f)
                    )

                }

            }
        }
    }
}
