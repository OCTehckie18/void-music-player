package com.example.funmusicplayer.mains

import android.net.Uri
import androidx.compose.ui.graphics.Color

// theme constants
val VoidBlack = Color(color = 0xFF070709)
val GlassSurface = Color(color = 0xFFFFFFFF).copy(alpha = 0.05f)

// core nav state
enum class AppScreen {
    LIST, PLAYER, PROFILE
}

// a musical track w a bespoke "Harmonic Luminescence" color profile
data class Track(
    val title: String,
    val artist: String,
    val durationMs: Long,
    val primaryColor: Color,
    val primaryGradient: List<Color>,
    val accentGradient: List<Color>,
    val logoStyle: Int,
    val contentUri: Uri? = null
)






