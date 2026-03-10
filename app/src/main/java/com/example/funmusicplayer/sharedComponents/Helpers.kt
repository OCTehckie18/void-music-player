package com.example.funmusicplayer.sharedComponents

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.funmusicplayer.mains.Track
import com.example.funmusicplayer.themeConstants.generateTrackStyle

// truncate in playlist screen
fun truncateText(text: String): String{
    val words = text.split(" ").filter {
        it.isNotBlank()
    }
    return if (words.size > 2) "${words[0]} ${words[1]}..." else text
}

fun getTrackFromUri(context: Context, uri: Uri, index: Int): Track {
    val retriever = MediaMetadataRetriever()

    return try {
        retriever.setDataSource(context, uri)

        val title = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown Title"
        val artist =   retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        val duration = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

        val style = generateTrackStyle(index)
        Track(title, artist, duration,
            style.color, style.primaryGradient,
            style.accentGradient,
            style.logoStyle, uri)

    } catch (e: Exception){
        val style = generateTrackStyle(index)
        Track("Error loading",
            "Unknown",
            0L,
            style.color,
            style.primaryGradient,
            style.accentGradient,
            style.logoStyle,
            uri)
    } finally {
        retriever.release()
    }

}