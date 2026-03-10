package com.example.funmusicplayer.sharedComponents


import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun TrackLogo(
    style: Int,
    modifier:  Modifier = Modifier,
    isMini: Boolean = false
){
    Canvas(
        modifier = modifier
    ){
        val foilBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFD700).copy(alpha = 0.8f),
                Color(0xFFFFA500).copy(alpha = 0.9f),
                Color(0xFFFF8C00).copy(alpha = 0.6f)
            ),
            start = Offset(x=0f, y=0f),
            end = Offset(x=size.width, y=size.height)
        )
        val center = Offset(
            x=size.width/2, y=size.height/2
        )
        val radius = size.width/2

        if (!isMini){
            drawCircle(
                brush = foilBrush,
                radius = radius * 0.95f,
                center = center,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(8f, 12f),
                        phase = 0f
                    )
                )
            )
        }

        drawCircle(
            brush = foilBrush,
            radius = radius * 0.95f,
            center = center,
            style = Stroke(
                width = if (isMini) 2f else 4f
            )
        )

        val path = Path()
        val w = size.width
        val h = size.height

        when(style % 6){
            0 -> {
                path.moveTo(w * 0.5f, h * 0.25f); path.lineTo(w * 0.25f, h * 0.75f)
                path.moveTo(w * 0.5f, h * 0.25f); path.lineTo(w * 0.75f, h * 0.75f)
                path.moveTo(w * 0.35f, h * 0.55f); path.lineTo(w * 0.65f, h * 0.55f)
                path.moveTo(w * 0.15f, h * 0.35f); path.lineTo(w * 0.5f, h * 0.85f);
                path.lineTo(w * 0.85f, h * 0.35f)
            }

            1 -> {
                path.moveTo(w * 0.5f, h * 0.25f); path.lineTo(w * 0.25f, h * 0.75f)
                path.lineTo(w * 0.75f, h * 0.75f);

                path.close()

                path.moveTo(w * 0.5f, h * 0.4f); path.lineTo(w * 0.5f, h * 0.75f)
            }

            2 -> {
                path.moveTo(w * 0.5f, h * 0.25f); path.lineTo(w * 0.7f, h * 0.35f)
                path.lineTo(w * 0.7f, h * 0.65f); path.lineTo(w * 0.5f, h * 0.75f)
                path.lineTo(w * 0.3f, h * 0.65f); path.lineTo(w * 0.3f, h * 0.35f);

                path.close()

                path.moveTo(w * 0.5f, h * 0.5f); path.lineTo(w * 0.5f, h * 0.75f)
                path.moveTo(w * 0.5f, h * 0.5f); path.lineTo(w * 0.3f, h * 0.35f)
                path.moveTo(w * 0.5f, h * 0.5f); path.lineTo(w * 0.7f, h * 0.35f)

            }

            3 -> {
                path.moveTo(w * 0.5f, h * 0.2f); path.lineTo(w * 0.8f, h * 0.5f)
                path.lineTo(w * 0.5f, h * 0.8f); path.lineTo(w * 0.2f, h * 0.5f);

                path.close()

                path.moveTo(w * 0.35f, h * 0.35f); path.lineTo(w * 0.65f, h * 0.65f)
                path.moveTo(w * 0.65f, h * 0.35f); path.lineTo(w * 0.35f, h * 0.65f)
            }

            4 -> {
                path.moveTo(w * 0.65f, h * 0.25f); path.lineTo(w * 0.35f, h * 0.25f)
                path.lineTo(w * 0.65f, h * 0.5f); path.lineTo(w * 0.35f, h * 0.5f)
                path.lineTo(w * 0.65f, h * 0.75f); path.lineTo(w * 0.35f, h * 0.75f)
            }

            5 -> {
                path.moveTo(w * 0.3f, h * 0.25f); path.lineTo(w * 0.7f, h * 0.25f)
                path.lineTo(w * 0.3f, h * 0.75f); path.lineTo(w * 0.7f, h * 0.75f);

                path.close()
            }

        }

        drawPath(
            path = path,
            brush = foilBrush,
            style = Stroke(
                width = if (isMini) 3f else 6f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

    }
}