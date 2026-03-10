package com.example.funmusicplayer.sharedComponents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.funmusicplayer.mains.GlassSurface
import com.example.funmusicplayer.mains.VoidBlack


@Composable
fun ProfileAvatar(
    themeColor: Color,
    showDot: Boolean,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .background(color = GlassSurface)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.5f),
                shape = CircleShape
            )
    ){
        Canvas(modifier = Modifier.fillMaxSize()){

            drawCircle(
                color = themeColor.copy(alpha = 0.2f),
                radius = size.width/2
            )

            drawCircle(
                color = themeColor,
                radius = size.width/4,
                center = Offset(
                    x = size.width/2,
                    y = size.height/3
                )
            )
            drawArc(
                color = themeColor.copy(alpha = 0.8f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                topLeft =
                    Offset(
                        x = size.width*0.15f,
                        y = size.height*0.45f
                    ),
                    size = Size(
                        width = size.width*0.7f,
                        height = size.height*0.7f
                    ),
                    style = Stroke(
                        width = size.width*0.08f,
                        cap = StrokeCap.Round
                    )
            )
        }

        if (showDot){
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .offset(
                        x = (-2).dp,
                        y = 2.dp
                    )
                    .size(size = 10.dp)
                    .clip(shape = CircleShape)
                    .background(color = themeColor)
                    .border(
                        width = 2.dp,
                        color = VoidBlack,
                        shape = CircleShape
                    )
                    .shadow(
                        elevation = 4.dp,
                        spotColor = themeColor
                    )
            )
        }
    }
}