package com.example.funmusicplayer.themeConstants

import androidx.compose.ui.graphics.Color
import kotlin.collections.listOf

data class TrackStyle(
    val color: Color,
    val primaryGradient: List<Color>,
    val accentGradient: List<Color>,
    val logoStyle: Int)


//helper to generate aesthetic colors for random local tracks
fun generateTrackStyle(index: Int): TrackStyle{
    val styles = listOf(
        TrackStyle(Color(0xFF00E5FF), listOf(Color(0xFF001122), Color(0xFF0088FF)), listOf(Color(0xFF00E5FF), Color(0xFF00FF9D)), 0),
        TrackStyle(Color(0xFFFF3366), listOf(Color(0xFF1A0011), Color(0xFFCC0044)), listOf(Color(0xFFFF3366), Color(0xFFFF7755)), 1),
        TrackStyle(Color(0xFFFFB800), listOf(Color(0xFF2A0D00), Color(0xFFCC5500)), listOf(Color(0xFFFF8800), Color(0xFFFFD700)), 2),
        TrackStyle(Color(0xFF9900FF), listOf(Color(0xFF0D001A), Color(0xFF5500CC)), listOf(Color(0xFF9900FF), Color(0xFFFF00AA)), 3),
        TrackStyle(Color(0xFF00FF66), listOf(Color(0xFF001A0D), Color(0xFF009944)), listOf(Color(0xFF00FF66), Color(0xFFCCFF00)), 4)

    )

    return styles[index % styles.size]
}
