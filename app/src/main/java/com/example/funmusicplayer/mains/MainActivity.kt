package com.example.funmusicplayer.mains

import android.app.Activity
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.example.funmusicplayer.mediaControls.ControlAction
import com.example.funmusicplayer.mediaControls.ControlButton
import com.example.funmusicplayer.mediaControls.PlayerControls
import com.example.funmusicplayer.sharedComponents.GlassHeader
import com.example.funmusicplayer.sharedComponents.ProfileAvatar
import com.example.funmusicplayer.sharedComponents.TrackList
import com.example.funmusicplayer.sharedComponents.TrackLogo
import com.example.funmusicplayer.sharedComponents.VinylRecord
import com.example.funmusicplayer.sharedComponents.getTrackFromUri
import com.example.funmusicplayer.sharedComponents.truncateText
import com.example.funmusicplayer.ui.theme.VoidMusicPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoidMusicPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicPlayer(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MusicPlayer(modifier: Modifier) {
    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var playlist by remember { mutableStateOf<List<Track>>(emptyList()) }
    var currentTrackIndex by remember { mutableIntStateOf(-1) }
    val currentTrack =
        if (currentTrackIndex in playlist.indices) playlist[currentTrackIndex] else null

    // Media Player Setup
    val mediaPlayer = remember {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA).build()
            )
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var currentTimeMs by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer.release() }
    }

    fun playTrack(track: Track) {
        track.contentUri?.let { uri ->
            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(context, uri)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    it.start()
                    isPlaying = true
                }
                mediaPlayer.setOnCompletionListener {
                    if (currentTrackIndex < playlist.size - 1) {
                        currentTrackIndex++
                        playTrack(playlist[currentTrackIndex])
                    } else {
                        isPlaying = false
                    }
                }
                mediaPlayer.setOnErrorListener { _, _, _ -> isPlaying = false; true }
            } catch (e: Exception) {
                isPlaying = false
            }
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            if (mediaPlayer.isPlaying) currentTimeMs = mediaPlayer.currentPosition.toLong()
            delay(100)
        }
    }

    // File Picker Launcher
    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch(Dispatchers.IO) {
                val startIndex = playlist.size
                val selectedTracks = uris.mapIndexed { index, uri ->
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                    }
                    getTrackFromUri(context, uri, startIndex + index)
                }
                withContext(Dispatchers.Main) {
                    playlist = playlist + selectedTracks
                    if (currentTrackIndex == -1 && selectedTracks.isNotEmpty()) {
                        currentTrackIndex = startIndex
                        playTrack(selectedTracks[0])
                    }
                }
            }
        }
    }

    var currentScreen by remember { mutableStateOf(AppScreen.LIST) }
    var stage by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }

    fun playOpeningChoreography() {
        if (isAnimating) return
        coroutineScope.launch {
            isAnimating = true
            currentScreen = AppScreen.PLAYER
            stage = 1; delay(550)
            stage = 2; delay(600)
            stage = 3; isAnimating = false
        }
    }

    fun playClosingChoreography() {
        if (isAnimating) return
        coroutineScope.launch {
            isAnimating = true
            stage = 2; delay(550)
            stage = 1; delay(500)
            stage = 0; currentScreen = AppScreen.LIST
            delay(550); isAnimating = false
        }
    }

    // Handle System Back Button
    BackHandler(enabled = currentScreen != AppScreen.LIST) {
        if (currentScreen == AppScreen.PLAYER) playClosingChoreography()
        else if (currentScreen == AppScreen.PROFILE) currentScreen = AppScreen.LIST
    }

    // Dynamic Color Engine
    val ambientThemeColor by animateColorAsState(
        currentTrack?.primaryColor ?: Color.Gray,
        tween(1200, easing = FastOutSlowInEasing),
        label = "ambient_theme"
    )
    val sleeveColor1 by animateColorAsState(
        currentTrack?.primaryGradient?.get(0) ?: VoidBlack,
        tween(1200),
        label = "sleeve_color1"
    )
    val sleeveColor2 by animateColorAsState(
        currentTrack?.primaryGradient?.get(1) ?: VoidBlack,
        tween(1200),
        label = "sleeve_color2"
    )
    val animatedSleeveGradient = listOf(sleeveColor1, sleeveColor2)

    val spatialGlide = spring<Rect>(dampingRatio = 0.8f, stiffness = 80f)
    val physicsSlideFloat = spring<Float>(dampingRatio = 0.75f, stiffness = 90f)
    val physicsSlideDp = spring<Dp>(dampingRatio = 0.75f, stiffness = 90f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = VoidBlack)
    ) {
        val bgAlpha by animateFloatAsState(
            if (currentScreen == AppScreen.PLAYER) 1f else 0f,
            tween(500),
            label = "bg_alpha"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = bgAlpha }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ambientThemeColor.copy(alpha = 0.2f),
                            VoidBlack,
                            VoidBlack
                        )
                    )
                )
                .blur(80.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        )

        SharedTransitionLayout {
            val sharedTransitionScope = this
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
                label = "screen_nav"
            ) { targetScreen ->
                val animatedVisibilityScope = this
                when (targetScreen) {
                    AppScreen.LIST -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val listTopPaddingDp = WindowInsets.statusBars.asPaddingValues()
                                .calculateTopPadding() + 108.dp
                            if (playlist.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("No music selected", color = Color.White)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { musicPickerLauncher.launch(arrayOf("audio/*")) },
                                            colors = ButtonDefaults.buttonColors(containerColor = GlassSurface)
                                        ) { Text(text = "Select Music", color = Color.White) }
                                    }
                                }
                            } else {
                                TrackList(
                                    tracks = playlist,
                                    currentTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    listTopPadding = listTopPaddingDp,
                                    onTrackSelect = { track ->
                                        currentTrackIndex =
                                            playlist.indexOf(track); playTrack(track)
                                    })
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .fillMaxWidth()
                            ) {
                                GlassHeader(
                                    sharedTransitionScope,
                                    animatedVisibilityScope,
                                    ambientThemeColor,
                                    { currentScreen = AppScreen.PROFILE },
                                    { musicPickerLauncher.launch(arrayOf("audio/*")) })
                            }
                            currentTrack?.let { track ->
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                                        .fillMaxWidth()
                                        .height(72.dp)
                                        .background(Color.Black, RoundedCornerShape(20.dp))
                                        .background(GlassSurface, RoundedCornerShape(20.dp))
                                        .border(
                                            1.dp,
                                            ambientThemeColor.copy(alpha = 0.3f),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { playOpeningChoreography() }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    with(sharedTransitionScope) {
                                        Box(
                                            modifier = Modifier
                                                .sharedElement(
                                                    rememberSharedContentState("album_sleeve"),
                                                    animatedVisibilityScope,
                                                    boundsTransform = { _, _ -> spatialGlide })
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    Brush.linearGradient(animatedSleeveGradient)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TrackLogo(track.logoStyle, Modifier.size(28.dp), true)
                                        }
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            truncateText(track.title),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            truncateText(track.artist),
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    ControlButton(
                                        ControlAction.PLAY_PAUSE,
                                        track.accentGradient,
                                        isPlaying,
                                        48.dp
                                    ) {
                                        if (isPlaying) mediaPlayer.pause() else mediaPlayer.start(); isPlaying =
                                        !isPlaying
                                    }
                                }
                            }
                        }
                    }

                    AppScreen.PLAYER -> {
                        currentTrack?.let { track ->
                            val density = LocalDensity.current
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 100.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(340.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val isExtracted = stage >= 2
                                    val isFrontAndCenter = stage >= 3

                                    val vinylScale by animateFloatAsState(
                                        if (isFrontAndCenter) 1.1f else 1f,
                                        physicsSlideFloat
                                    )
                                    val vinylOffsetX by animateDpAsState(
                                        if (isFrontAndCenter) 0.dp else if (isExtracted) 100.dp else 0.dp,
                                        physicsSlideDp
                                    )
                                    val sleeveOffsetX by animateDpAsState(
                                        if (isExtracted) (-120).dp else 0.dp,
                                        physicsSlideDp
                                    )
                                    val sleeveAlpha by animateFloatAsState(
                                        if (isExtracted) 0f else 1f,
                                        tween(500)
                                    )

                                    // 1. THE VINYL RECORD
                                    VinylRecord(
                                        isPlaying = isPlaying && isExtracted,
                                        themeColor = ambientThemeColor,
                                        stampGradient = animatedSleeveGradient,
                                        modifier = Modifier
                                            .size(280.dp)
                                            .zIndex(if (isExtracted) 5f else 0f)
                                            .graphicsLayer {
                                                translationX = with(density) { vinylOffsetX.toPx() }
                                                scaleX = vinylScale
                                                scaleY = vinylScale
                                            }
                                            .clickable { playClosingChoreography() }
                                    )

                                    // 2. THE SLEEVE
                                    if (sleeveAlpha > 0f) {
                                        with(sharedTransitionScope) {
                                            Box(
                                                modifier = Modifier
                                                    .sharedElement(
                                                        rememberSharedContentState("album_sleeve"),
                                                        animatedVisibilityScope,
                                                        boundsTransform = { _, _ -> spatialGlide })
                                                    .size(300.dp)
                                                    .zIndex(1f)
                                                    .graphicsLayer {
                                                        translationX =
                                                            with(density) { sleeveOffsetX.toPx() }
                                                        alpha = sleeveAlpha
                                                    }
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        Brush.linearGradient(animatedSleeveGradient)
                                                    )
                                            ) {
                                                TrackLogo(
                                                    track.logoStyle,
                                                    Modifier
                                                        .align(Alignment.Center)
                                                        .size(140.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(48.dp))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                ) {
                                    Text(
                                        text = track.title,
                                        color = Color.White,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = track.artist,
                                        color = ambientThemeColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(Modifier.height(32.dp))
                                PlayerControls(
                                    track,
                                    isPlaying,
                                    stage >= 1,
                                    currentTimeMs,
                                    {
                                        if (isPlaying) mediaPlayer.pause() else mediaPlayer.start(); isPlaying =
                                        !isPlaying
                                    },
                                    {
                                        if (currentTrackIndex > 0) {
                                            currentTrackIndex--; playTrack(playlist[currentTrackIndex])
                                        }
                                    },
                                    {
                                        if (currentTrackIndex < playlist.size - 1) {
                                            currentTrackIndex++; playTrack(playlist[currentTrackIndex])
                                        }
                                    })
                            }
                        }
                    }

                    AppScreen.PROFILE -> {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(top = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            with(sharedTransitionScope) {
                                ProfileAvatar(
                                    ambientThemeColor,
                                    false,
                                    Modifier
                                        .sharedElement(
                                            rememberSharedContentState("profile_avatar"),
                                            animatedVisibilityScope
                                        )
                                        .size(160.dp)
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                            Text(
                                "VOID MUSIC PLAYER",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(Modifier.weight(1f))

                            Text(
                                "Omkaar Chakraborty",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                "2547237 | 3 MCA B",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(Modifier.weight(1f))
                            Box(
                                Modifier
                                    .padding(bottom = 64.dp)
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(GlassSurface)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                    .clickable { currentScreen = AppScreen.LIST },
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(Modifier.size(24.dp)) {
                                    drawLine(
                                        Color.White,
                                        Offset(0f, 0f),
                                        Offset(size.width, size.height),
                                        2.dp.toPx(),
                                        StrokeCap.Round
                                    )
                                    drawLine(
                                        Color.White,
                                        Offset(0f, size.height),
                                        Offset(size.width, 0f),
                                        2.dp.toPx(),
                                        StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
