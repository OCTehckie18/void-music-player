package com.example.funmusicplayer.sharedComponents

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.funmusicplayer.mains.GlassSurface
import com.example.funmusicplayer.mains.Track
import com.example.funmusicplayer.themeConstants.formatTimeMs

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable

//scrolling tracklist  component
fun TrackList(
    tracks: List<Track>,
    currentTrack: Track?,
    isPlaying: Boolean,
    listTopPadding: Dp,
    onTrackSelect: (Track) -> Unit,
    modifier: Modifier = Modifier
    ) {

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = listTopPadding,
            bottom = 140.dp
        ),
        verticalArrangement = Arrangement.spacedBy(space = 4.dp)
    ) {
        itemsIndexed(
            items = tracks,
            key = { _, track -> track.title + track.artist + track.contentUri.toString() }
        ){
            index, track ->

            val isSelected = track == currentTrack
            val interactionSource = remember { MutableInteractionSource() }
            val transition = updateTransition(targetState = isSelected, label = "track_selection")

            val layoutSpringDp = spring<Dp>(
                dampingRatio = 0.7f,
                stiffness = 250f
            )
            val visualSpring = spring<Float>(
                dampingRatio = 0.6f,
                stiffness = 200f
            )
            val fadeTween = tween<Float>(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )

            // card UI variables
            val cardHeight by transition.animateDp(
                transitionSpec = { layoutSpringDp }, label = "height"
            ){
                if (it) 100.dp else 64.dp
            }

            val cardScale by transition.animateFloat(
                transitionSpec = { visualSpring },
                label = "scale"
            ) {
                if (it) 1.0f else 0.95f
            }

            val cardAlpha by transition.animateFloat(
                transitionSpec = { fadeTween },
                label = "alpha"
            ) {
                if (it) 1f else 0.4f
            }

            val glassOpacity by transition.animateFloat(
                transitionSpec = { fadeTween },
                label = "glass"
            ) {
                if (it) 1f else 0f
            }

            val numScale by transition.animateFloat(
                transitionSpec = { visualSpring },
                label = "num_scale"
            ) {
                if (it) 5f else 1f
            }

            val numAlpha by transition.animateFloat(
                transitionSpec = { fadeTween },
                label = "num_alpha"
            ) {
                if (it) 0.15f else 0.4f
            }

            val textOffsetX by transition.animateDp(
                transitionSpec = { layoutSpringDp },
                label = "text_x"
            ) {
                if (it) 124.dp else 44.dp
            }

            // mainframe UI

            Box(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(height = cardHeight.coerceAtLeast(minimumValue = 0.dp))
                    .graphicsLayer{
                        scaleX = cardScale
                        scaleY = cardScale
                        alpha = cardAlpha
                    }
                    .background(
                        color = GlassSurface.copy(
                            alpha = GlassSurface.alpha * glassOpacity
                        ),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = track.primaryColor.copy(
                            alpha = 0.3f*glassOpacity
                        ),
                        shape = RoundedCornerShape(size = 24.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {onTrackSelect(track)}
                    )
                    .padding(horizontal = 20.dp)
            ){

                // Morphing Track Number
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .offset(x=16.dp)
                ){
                    Text(
                        text = "${index+1}".padStart(
                            length=2,
                            padChar = '0'
                        ),
                        color = if (isSelected) track.primaryColor else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .requiredSize(size = 0.dp)
                            .wrapContentSize(
                                unbounded = true,
                                align = Alignment.CenterStart
                            )
                            .graphicsLayer{
                                scaleX = numScale
                                scaleY = numScale
                                alpha = numAlpha
                                transformOrigin = TransformOrigin(
                                    pivotFractionX = 0f,
                                    pivotFractionY = 0.5f
                                )
                            }
                    )
                }

                // left neon activity indicator
                val indicatorHeight by transition.animateDp(
                    transitionSpec = { layoutSpringDp },
                    label = "ind_h"
                ) {
                    if (it) 48.dp else 12.dp
                }

                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .width(4.dp)
                        .height(height = indicatorHeight.coerceAtLeast(minimumValue = 0.dp))
                        .clip(shape = CircleShape)
                        .background(
                            color = if (isSelected) track.primaryColor else Color.White.copy(
                                alpha = 0.3f
                            )
                        )
                )

                // track title and artist
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterStart)
                        .offset(x = textOffsetX),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = truncateText(track.title),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold
                    )

                    Text(
                        text = truncateText(track.artist),
                        color = if (isSelected) track.primaryColor else Color.White.copy(
                            alpha=0.6f
                        ),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                //playback visualizer/duration
                Box(
                    modifier = Modifier.align(
                        alignment = Alignment.CenterEnd
                    )
                ){
                    Crossfade(
                        targetState = isSelected,
                        animationSpec = tween(
                            durationMillis = 300
                        ),
                        label = "eq_fade"
                    ) {
                        selected ->
                        if (selected) {
                            AudioVisualizer(
                                color = track.primaryColor,
                                isPlaying = isPlaying
                            )
                        } else {
                            Text(
                                text = formatTimeMs(
                                    ms = track.durationMs
                                ),
                                color = Color.White.copy(
                                    alpha = 0.4f
                                ),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

        }

    }
}

