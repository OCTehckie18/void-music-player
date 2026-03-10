package com.example.funmusicplayer.themeConstants

// convert song milliseconds to mm:ss
fun formatTimeMs(ms: Long): String{
    val totalSeconds = ms/1000
    val m = (totalSeconds/60).toString().padStart(length = 2, padChar = '0')
    val s = (totalSeconds%60).toString().padStart(length = 2, padChar = '0')

    return "$m:$s"
}