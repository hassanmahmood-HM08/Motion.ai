package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.VideoProject
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotionAppScreen(
    viewModel: MotionViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isProUser by viewModel.isProUser.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val selectedProject by viewModel.selectedProject.collectAsStateWithLifecycle()

    var isDarkMode by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Base background colors and brushes
    val bgColors = if (isDarkMode) {
        listOf(Color(0xFF070A13), Color(0xFF0F1424), Color(0xFF070A13))
    } else {
        listOf(Color(0xFFFAFBFF), Color(0xFFF1F3FE), Color(0xFFFAFBFF))
    }
    val backgroundBrush = Brush.linearGradient(colors = bgColors)
    val cardBg = if (isDarkMode) Color(0xFF151B2C) else Color(0xFFFFFFFF)
    val dividerColor = if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7)
    val textPrimary = if (isDarkMode) TextPrimaryDark else TextPrimaryLight
    val textSecondary = if (isDarkMode) TextSecondaryDark else TextSecondaryLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(PrimaryColor, SecondaryColor)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White)
                                    .graphicsLayer { rotationZ = 45f }
                            )
                        }
                        Text(
                            text = "Motion.ai",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    // Theme Switch
                    IconButton(onClick = { isDarkMode = !isDarkMode }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (isDarkMode) AccentDark else PrimaryColor
                        )
                    }

                    // User Profile or Login Trigger
                    if (isLoggedIn) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            if (isProUser) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Brush.linearGradient(colors = listOf(SecondaryColor, AccentColor)))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryColor.copy(alpha = 0.15f))
                                    .border(1.dp, PrimaryColor, CircleShape)
                                    .clickable { viewModel.setTab(AppTab.SETTINGS) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userEmail.take(2).uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryColor
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.setTab(AppTab.SETTINGS) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("login_action_button")
                        ) {
                            Text("Log In", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) Color(0xFF0B0F19) else Color(0xFFFFFFFF),
                    scrolledContainerColor = if (isDarkMode) Color(0xFF0B0F19) else Color(0xFFFFFFFF)
                ),
                modifier = Modifier.shadow(1.dp)
            )
        },
        bottomBar = {
            if (!isWideScreen) {
                NavigationBar(
                    containerColor = if (isDarkMode) Color(0xFF0B0F19) else Color(0xFFFFFFFF),
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val tabs = listOf(
                        Triple(AppTab.HOME, "Home", Icons.Rounded.Home),
                        Triple(AppTab.GENERATE, "Generate", Icons.Rounded.AutoAwesome),
                        Triple(AppTab.VIDEOS, "My Videos", Icons.Rounded.VideoLibrary),
                        Triple(AppTab.PRICING, "Pricing", Icons.Rounded.LocalAtm),
                        Triple(AppTab.SETTINGS, "Account", Icons.Rounded.Person)
                    )
                    tabs.forEach { (tab, label, icon) ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                viewModel.clearSelectedProject()
                                viewModel.setTab(tab)
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) PrimaryColor else textSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) PrimaryColor else textSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = PrimaryColor.copy(alpha = 0.08f)
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundBrush)
        ) {
            // Sidebar for wide screens (Tablet/Landscape)
            if (isWideScreen) {
                NavigationRail(
                    containerColor = if (isDarkMode) Color(0xFF0B0F19) else Color(0xFFFFFFFF),
                    modifier = Modifier
                        .fillMaxHeight()
                        .shadow(2.dp)
                        .testTag("side_navigation_rail")
                ) {
                    val tabs = listOf(
                        Triple(AppTab.HOME, "Home", Icons.Rounded.Home),
                        Triple(AppTab.GENERATE, "Generate", Icons.Rounded.AutoAwesome),
                        Triple(AppTab.VIDEOS, "My Videos", Icons.Rounded.VideoLibrary),
                        Triple(AppTab.PRICING, "Pricing", Icons.Rounded.LocalAtm),
                        Triple(AppTab.SETTINGS, "Account", Icons.Rounded.Person)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    tabs.forEach { (tab, label, icon) ->
                        val selected = currentTab == tab
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                viewModel.clearSelectedProject()
                                viewModel.setTab(tab)
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) PrimaryColor else textSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) PrimaryColor else textSecondary
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = PrimaryColor.copy(alpha = 0.08f)
                            )
                        )
                    }
                }
            }

            // Main Viewport Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // If a project is selected, show the video preview/player details screen overlay!
                if (selectedProject != null) {
                    VideoDetailsScreen(
                        project = selectedProject!!,
                        isDarkMode = isDarkMode,
                        cardBg = cardBg,
                        dividerColor = dividerColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onBack = { viewModel.clearSelectedProject() },
                        onDelete = { viewModel.deleteProject(it) }
                    )
                } else {
                    // Standard tab display
                    when (currentTab) {
                        AppTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            dividerColor = dividerColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isWideScreen = isWideScreen
                        )
                        AppTab.GENERATE -> GenerateScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isWideScreen = isWideScreen
                        )
                        AppTab.VIDEOS -> RecentVideosScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isWideScreen = isWideScreen
                        )
                        AppTab.PRICING -> PricingScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isWideScreen = isWideScreen
                        )
                        AppTab.SETTINGS -> SettingsAuthScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            cardBg = cardBg,
                            dividerColor = dividerColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isWideScreen = isWideScreen
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. HOME SCREEN - HERO, PRESETS, HOW IT WORKS, FEATURES, FAQ
// -------------------------------------------------------------
@Composable
fun HomeScreen(
    viewModel: MotionViewModel,
    isDarkMode: Boolean,
    cardBg: Color,
    dividerColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWideScreen: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_container"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(PrimaryColor.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = "Trusted",
                            tint = SuccessColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "TRUSTED BY 10,000+ CREATORS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Turn Any Image into\nStunning AI Videos.",
                    fontSize = if (isWideScreen) 44.sp else 32.sp,
                    lineHeight = if (isWideScreen) 50.sp else 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.8).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Upload a photo, choose a motion style, and let AI transform your image into a cinematic video in just seconds.",
                    fontSize = 15.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(max = 550.dp)
                        .alpha(0.85f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.setTab(AppTab.GENERATE) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(16.dp), clip = false)
                            .testTag("hero_generate_button")
                    ) {
                        Text("Generate Video", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            // Try Demo with first preset
                            viewModel.selectPreset(viewModel.demoPresets.first())
                        },
                        border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333E60) else Color(0xFFCBD5E1)),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("hero_try_demo_button")
                    ) {
                        Text(
                            "Try Demo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                }
            }
        }

        // Horizontal Row of Presets / "Try Demo" Carousel
        item {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Ready Presets",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.demoPresets) { preset ->
                        PresetDemoCard(
                            preset = preset,
                            cardBg = cardBg,
                            isDarkMode = isDarkMode,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onSelect = { viewModel.selectPreset(preset) }
                        )
                    }
                }
            }
        }

        // How It Works Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How It Works",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                val steps = listOf(
                    Triple("Step 1", "Upload Image", "Pick a photo from your gallery or choose a demo preset."),
                    Triple("Step 2", "AI Motion Style", "Configure camera motion (Zoom, Pan, Orbit) and intensity."),
                    Triple("Step 3", "Generate & Download", "Watch AI synthesize realistic video. Export in 4K.")
                )

                if (isWideScreen) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        steps.forEachIndexed { idx, (step, title, desc) ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(cardBg)
                                    .border(1.dp, dividerColor, RoundedCornerShape(24.dp))
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (idx + 1).toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryColor,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    desc,
                                    fontSize = 13.sp,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        steps.forEachIndexed { idx, (step, title, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(cardBg)
                                    .border(1.dp, dividerColor, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (idx + 1).toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryColor,
                                        fontSize = 15.sp
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textPrimary)
                                    Text(desc, fontSize = 12.sp, color = textSecondary, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Features Section (6 Cards)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Next-Generation AI Capabilities",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                val features = listOf(
                    FeatureData(Icons.Rounded.ElectricBolt, "Lightning Fast Generation", "Synthesize high-framerate clips in under 15 seconds with custom servers."),
                    FeatureData(Icons.Rounded.MotionPhotosOn, "Realistic Motion", "Optical depth mapping simulates dynamic fluid simulation and physics."),
                    FeatureData(Icons.Rounded.VideoCameraBack, "AI Camera Movement", "Programmatic sweeps, cranes, dolly moves, and multi-axis camera orbits."),
                    FeatureData(Icons.Rounded.HighQuality, "4K Video Export", "Lossless upscaling preserves textures, cinematic lighting, and resolution."),
                    FeatureData(Icons.Rounded.Face, "Face & Character Consistency", "Keep character coordinates identical across extreme panning vectors."),
                    FeatureData(Icons.Rounded.CloudDone, "Secure Cloud Processing", "Images are deleted safely post-render. SSL end-to-end encrypted.")
                )

                if (isWideScreen) {
                    val chunked = features.chunked(3)
                    chunked.forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { feature ->
                                Box(modifier = Modifier.weight(1f)) {
                                    FeatureCard(feature, cardBg, dividerColor, textPrimary, textSecondary)
                                }
                            }
                        }
                    }
                } else {
                    features.forEach { feature ->
                        FeatureCard(feature, cardBg, dividerColor, textPrimary, textSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Testimonials
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Loved by Filmmakers & Creators",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val testimonials = listOf(
                    TestimonialData("Alex Mercer", "Lead VFX Artist", "The Dolly motion style is incredible. Parallax background layer processing is indistinguishable from expensive motion-rigs.", 5, "AM"),
                    TestimonialData("Sofia Chen", "Creative Producer", "It completely changed how we prototype storyboards. Rendering static designs into 15s videos takes seconds.", 5, "SC")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    testimonials.forEach { tm ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(cardBg)
                                .border(1.dp, dividerColor, RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(tm.initials, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                }
                                Column {
                                    Text(tm.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                    Text(tm.role, fontSize = 10.sp, color = textSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row {
                                repeat(tm.stars) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Star",
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\"${tm.text}\"",
                                fontSize = 12.sp,
                                color = textSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Accordion FAQ Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val faqs = listOf(
                    "How long does it take to convert an image?" to "Standard 5s videos generate in under 10 seconds. 15s Pro videos take slightly longer to render optical vectors correctly.",
                    "Does this require expensive hardware?" to "Not at all. All video rendering is processed on our ultra-high performance cloud servers. You can run it on any standard mobile phone.",
                    "Can I export videos without watermark?" to "Yes, our Pro and Business plans support clean 4K rendering with no watermarks.",
                    "What formats are supported?" to "We accept high-quality PNG, JPG, and WEBP image uploads up to 20MB."
                )

                faqs.forEach { (q, a) ->
                    FaqAccordion(q, a, cardBg, dividerColor, textPrimary, textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Footer Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, dividerColor))
                    .background(cardBg)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Privacy Policy", fontSize = 12.sp, color = textSecondary, modifier = Modifier.clickable {})
                    Text("Terms of Service", fontSize = 12.sp, color = textSecondary, modifier = Modifier.clickable {})
                    Text("Contact Support", fontSize = 12.sp, color = textSecondary, modifier = Modifier.clickable {})
                    Text("API Docs", fontSize = 12.sp, color = textSecondary, modifier = Modifier.clickable {})
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.08f))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Share, "Share", tint = PrimaryColor, modifier = Modifier.size(16.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor.copy(alpha = 0.08f))
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.CloudQueue, "Cloud", tint = SecondaryColor, modifier = Modifier.size(16.dp))
                    }
                }

                Text(
                    text = "© 2026 Motion.ai Inc. All Rights Reserved. Secure cloud parsing.",
                    fontSize = 11.sp,
                    color = textSecondary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. GENERATE SCREEN - DASHBOARD VIEWPORT WITH CONTROLS & ANIMATION
// -------------------------------------------------------------
@Composable
fun GenerateScreen(
    viewModel: MotionViewModel,
    isDarkMode: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWideScreen: Boolean
) {
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val motionS by viewModel.motionStrength.collectAsStateWithLifecycle()
    val durationS by viewModel.durationSeconds.collectAsStateWithLifecycle()
    val aspect by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val cameraM by viewModel.cameraMotion.collectAsStateWithLifecycle()
    val creativityVal by viewModel.creativity.collectAsStateWithLifecycle()
    val promptText by viewModel.prompt.collectAsStateWithLifecycle()
    val negativePromptText by viewModel.negativePrompt.collectAsStateWithLifecycle()

    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val progress by viewModel.generationProgress.collectAsStateWithLifecycle()
    val statusText by viewModel.generationStatusText.collectAsStateWithLifecycle()

    val dividerColor = if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7)
    val context = LocalContext.current

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.selectedImageUri.value = uri.toString()
        }
    }

    if (isWideScreen) {
        // Horizontal Split-Pane for Tablets/Wide-view
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: Controls (Weight 1)
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(1.dp, if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7), RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("AI Motion Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                Divider(color = if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7))

                // Camera Motion Options
                Column {
                    Text("Camera Direction", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    val cameras = listOf("Zoom In", "Zoom Out", "Pan Left", "Pan Right", "Orbit", "Dolly")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(130.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(cameras) { cam ->
                            val active = cameraM == cam
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (active) PrimaryColor else if (isDarkMode) Color(0xFF1E2638) else Color(0xFFF3F4F6))
                                    .clickable { viewModel.cameraMotion.value = cam }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cam,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (active) Color.White else textPrimary
                                )
                            }
                        }
                    }
                }

                // Video Duration
                Column {
                    Text("Video Duration", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15).forEach { dur ->
                            val active = durationS == dur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (active) PrimaryColor else if (isDarkMode) Color(0xFF1E2638) else Color(0xFFF3F4F6))
                                    .clickable { viewModel.durationSeconds.value = dur }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${dur}s",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.White else textPrimary
                                )
                            }
                        }
                    }
                }

                // Aspect Ratio
                Column {
                    Text("Aspect Ratio", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("16:9", "9:16", "1:1").forEach { asp ->
                            val active = aspect == asp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (active) PrimaryColor else if (isDarkMode) Color(0xFF1E2638) else Color(0xFFF3F4F6))
                                    .clickable { viewModel.aspectRatio.value = asp }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = asp,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) Color.White else textPrimary
                                )
                            }
                        }
                    }
                }

                // Slider: Motion Strength
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Motion Strength", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("${(motionS * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                    Slider(
                        value = motionS,
                        onValueChange = { viewModel.motionStrength.value = it },
                        colors = SliderDefaults.colors(
                            activeTrackColor = PrimaryColor,
                            thumbColor = PrimaryColor
                        )
                    )
                }

                // Slider: Creativity (Temperature-like parameter)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Creativity Multiplier", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("${(creativityVal * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryColor)
                    }
                    Slider(
                        value = creativityVal,
                        onValueChange = { viewModel.creativity.value = it },
                        colors = SliderDefaults.colors(
                            activeTrackColor = SecondaryColor,
                            thumbColor = SecondaryColor
                        )
                    )
                }

                // Prompt Input Box
                Column {
                    Text("Creative Description Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { viewModel.prompt.value = it },
                        placeholder = { Text("E.g., cinematic panning shot with realistic lighting, volumetric dust particles...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Negative Prompt
                Column {
                    Text("Negative Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = negativePromptText,
                        onValueChange = { viewModel.negativePrompt.value = it },
                        placeholder = { Text("blurry, low quality, static, frame clipping...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Right Column: Workspace Preview area (Weight 1)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Upload card or Render card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(cardBg)
                        .border(
                            BorderStroke(
                                2.dp,
                                if (selectedImageUri == null) {
                                    Brush.sweepGradient(listOf(PrimaryColor, SecondaryColor, AccentColor))
                                } else {
                                    Brush.linearGradient(listOf(PrimaryColor, SecondaryColor))
                                }
                            ),
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri == null) {
                        // Empty upload card
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                                .padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryColor.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.CloudUpload, "Upload", tint = PrimaryColor, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Drag & Drop or Browse", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                            Text("Supports JPG, PNG or WEBP (Max 20MB)", fontSize = 11.sp, color = textSecondary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Select Image", fontSize = 12.sp)
                            }
                        }
                    } else {
                        // Selected image preview / rendering
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Source Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Glassmorphic status overlay during generation
                            if (isGenerating) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.65f))
                                        .blur(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = AccentDark,
                                            strokeWidth = 4.dp,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "$progress%",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = statusText,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                // Overlay info showing image selected
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessColor))
                                        Text("AI Ready", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.selectedImageUri.value = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Rounded.Close, "Remove", tint = Color.White)
                                }
                            }
                        }
                    }
                }

                // Render Action Button
                Button(
                    onClick = { viewModel.generateVideo {
                        Toast.makeText(context, "AI Video Generated Successfully!", Toast.LENGTH_SHORT).show()
                    } },
                    enabled = selectedImageUri != null && !isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        disabledContainerColor = textSecondary.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("generate_video_main_button"),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, "Sparkles")
                        Text("Generate AI Video", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        // Vertical Scroll View for Mobile devices
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Video Workspace",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Dynamic Interactive Canvas Upload Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(
                        BorderStroke(
                            2.dp,
                            if (selectedImageUri == null) {
                                Brush.sweepGradient(listOf(PrimaryColor, SecondaryColor, AccentColor))
                            } else {
                                Brush.linearGradient(listOf(PrimaryColor, SecondaryColor))
                            }
                        ),
                        RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.CloudUpload, "Upload", tint = PrimaryColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Select Image to Begin", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textPrimary)
                        Text("JPG, PNG or WEBP up to 20MB", fontSize = 11.sp, color = textSecondary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Browse Files", fontSize = 12.sp)
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Source Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        if (isGenerating) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.65f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = AccentDark, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("$progress%", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(statusText, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessColor))
                                    Text("AI Ready", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            IconButton(
                                onClick = { viewModel.selectedImageUri.value = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Rounded.Close, "Remove", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Quick Preset Demo Picker under upload (In case they want one)
            if (selectedImageUri == null) {
                Column {
                    Text("Or Try with Demo Presets:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(viewModel.demoPresets) { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardBg)
                                    .border(1.dp, dividerColor, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectPreset(preset) }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AsyncImage(
                                        model = preset.imageUrl,
                                        contentDescription = preset.title,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(preset.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // AI Controls Card
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .border(1.dp, dividerColor, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Settings Row: Duration & Aspect Ratio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration Column
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Duration", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDarkMode) Color(0xFF1B2336) else Color(0xFFF3F4F6))
                                .padding(2.dp)
                        ) {
                            listOf(5, 10, 15).forEach { dur ->
                                val selected = durationS == dur
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) PrimaryColor else Color.Transparent)
                                        .clickable { viewModel.durationSeconds.value = dur }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${dur}s",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Aspect Ratio Column
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text("Aspect Ratio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDarkMode) Color(0xFF1B2336) else Color(0xFFF3F4F6))
                                .padding(2.dp)
                        ) {
                            listOf("16:9", "9:16", "1:1").forEach { asp ->
                                val selected = aspect == asp
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) PrimaryColor else Color.Transparent)
                                        .clickable { viewModel.aspectRatio.value = asp }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = asp,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Slider: Motion Strength
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Motion Strength", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("${(motionS * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                    Slider(
                        value = motionS,
                        onValueChange = { viewModel.motionStrength.value = it },
                        colors = SliderDefaults.colors(
                            activeTrackColor = PrimaryColor,
                            thumbColor = PrimaryColor
                        )
                    )
                }

                // Camera direction picker
                Column {
                    Text("Camera Motion", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    val motions = listOf("Zoom In", "Zoom Out", "Pan Left", "Pan Right", "Orbit", "Dolly")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(motions) { m ->
                            val selected = cameraM == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) PrimaryColor else if (isDarkMode) Color(0xFF1B2336) else Color(0xFFF3F4F6))
                                    .clickable { viewModel.cameraMotion.value = m }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    m,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) Color.White else textPrimary
                                )
                            }
                        }
                    }
                }

                // Slider: Creativity
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("AI Creativity Multiplier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("${(creativityVal * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryColor)
                    }
                    Slider(
                        value = creativityVal,
                        onValueChange = { viewModel.creativity.value = it },
                        colors = SliderDefaults.colors(
                            activeTrackColor = SecondaryColor,
                            thumbColor = SecondaryColor
                        )
                    )
                }

                // Prompt Input
                Column {
                    Text("Describe the motion dynamics (Positive Prompt)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { viewModel.prompt.value = it },
                        placeholder = { Text("E.g., Slow cinematic panning shot, realistic reflection on water, high definition...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Negative prompt
                Column {
                    Text("Negative Prompt (Elements to avoid)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = negativePromptText,
                        onValueChange = { viewModel.negativePrompt.value = it },
                        placeholder = { Text("flickering, color shifting, morphing...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Generate Button
            Button(
                onClick = { viewModel.generateVideo {
                    Toast.makeText(context, "AI Video Generated Successfully!", Toast.LENGTH_SHORT).show()
                } },
                enabled = selectedImageUri != null && !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = textSecondary.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("generate_video_mobile_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AutoAwesome, "Sparkles")
                    Text("Generate AI Video", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // SSL Secure badge overlay helper
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (isDarkMode) Color(0xFF1E2638) else Color(0xFFF3F4F6))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "SSL Secure",
                        tint = SuccessColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "SSL SECURE PROCESSING • NO UNUSED STORAGE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. RECENT VIDEOS SCREEN - DASHBOARD GALLERY OF PAST PROJECTS
// -------------------------------------------------------------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentVideosScreen(
    viewModel: MotionViewModel,
    isDarkMode: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWideScreen: Boolean
) {
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("recent_videos_screen")
    ) {
        Text("My Videos Dashboard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        Text("Review and play your high-fidelity generated camera simulations.", fontSize = 12.sp, color = textSecondary)

        Spacer(modifier = Modifier.height(16.dp))

        if (projects.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VideoCameraFront,
                            contentDescription = "Empty",
                            tint = PrimaryColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No AI Videos Found", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Upload an image and choose a motion style in the Generate workspace tab to create your first video.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 300.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.setTab(AppTab.GENERATE) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Open Workspace", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Video Project List / Grid
            val cols = if (isWideScreen) 3 else 2
            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(projects) { project ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(1.dp, if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7), RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = { viewModel.selectProject(project) },
                                onLongClick = { viewModel.deleteProject(project) }
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(project.imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Project Thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Duration Tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${project.durationSeconds}s",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Aspect Ratio Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryColor)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = project.aspectRatio,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = project.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Camera: ${project.cameraMotion}",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3.1 VIDEO DETAILS SCREEN - PLAYER VIEWPORT WITH CUSTOM PHYSICS ANIMATION
// -------------------------------------------------------------
@Composable
fun VideoDetailsScreen(
    project: VideoProject,
    isDarkMode: Boolean,
    cardBg: Color,
    dividerColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onBack: () -> Unit,
    onDelete: (VideoProject) -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showFullscreen by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val progressAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = project.durationSeconds * 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    // Calculate motion offsets/transforms depending on CameraMotion option
    val scaleVal: Float
    val transX: Float
    val transY: Float
    val rotZ: Float

    when (project.cameraMotion) {
        "Zoom In" -> {
            scaleVal = 1f + (progressAnim * 0.18f * project.motionStrength)
            transX = 0f
            transY = 0f
            rotZ = 0f
        }
        "Zoom Out" -> {
            scaleVal = 1.18f - (progressAnim * 0.18f * project.motionStrength)
            transX = 0f
            transY = 0f
            rotZ = 0f
        }
        "Pan Left" -> {
            scaleVal = 1.15f
            transX = (progressAnim - 0.5f) * 45f * project.motionStrength
            transY = 0f
            rotZ = 0f
        }
        "Pan Right" -> {
            scaleVal = 1.15f
            transX = (0.5f - progressAnim) * 45f * project.motionStrength
            transY = 0f
            rotZ = 0f
        }
        "Orbit" -> {
            val angle = progressAnim * 2 * Math.PI
            scaleVal = 1.08f + (sin(angle).toFloat() * 0.04f * project.motionStrength)
            transX = sin(angle).toFloat() * 15f * project.motionStrength
            transY = cos(angle).toFloat() * 10f * project.motionStrength
            rotZ = sin(angle).toFloat() * 1.5f * project.motionStrength
        }
        "Dolly" -> {
            scaleVal = 1f + (progressAnim * 0.22f * project.motionStrength)
            transX = 0f
            transY = progressAnim * -12f * project.motionStrength
            rotZ = 0f
        }
        else -> {
            scaleVal = 1f
            transX = 0f
            transY = 0f
            rotZ = 0f
        }
    }

    val activeScale = if (isPlaying) scaleVal else 1f
    val activeTransX = if (isPlaying) transX else 0f
    val activeTransY = if (isPlaying) transY else 0f
    val activeRotZ = if (isPlaying) rotZ else 0f

    val context = LocalContext.current

    if (showFullscreen) {
        // Fullscreen player Dialog/Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Animated Canvas image
            AsyncImage(
                model = project.imageUri,
                contentDescription = "Fullscreen Video",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = activeScale
                        scaleY = activeScale
                        translationX = activeTransX
                        translationY = activeTransY
                        rotationZ = activeRotZ
                    },
                contentScale = ContentScale.Fit
            )

            // Overlays
            IconButton(
                onClick = { showFullscreen = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Rounded.FullscreenExit, "Exit Fullscreen", tint = Color.White)
            }

            // Controls bottom overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }

                // Scrubber linear indicator
                LinearProgressIndicator(
                    progress = { if (isPlaying) progressAnim else 0f },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .height(4.dp),
                    color = AccentDark,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )

                Text(
                    text = "${(progressAnim * project.durationSeconds).toInt()}s / ${project.durationSeconds}s",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        // Standard Detail view
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("video_details_screen")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, "Back", tint = textPrimary)
                }
                Text(project.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    onDelete(project)
                    onBack()
                    Toast.makeText(context, "Project deleted safely.", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Rounded.Delete, "Delete", tint = Color.Red.copy(alpha = 0.8f))
                }
            }

            // Interactive Video Player Box with dynamic AspectRatio frame bounding!
            val playerRatio = when (project.aspectRatio) {
                "16:9" -> 1.77f
                "9:16" -> 0.56f
                "1:1" -> 1f
                else -> 1.77f
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(playerRatio)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .border(1.dp, dividerColor, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Interactive dynamic camera physics canvas
                AsyncImage(
                    model = project.imageUri,
                    contentDescription = "Video Simulation player",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = activeScale
                            scaleY = activeScale
                            translationX = activeTransX
                            translationY = activeTransY
                            rotationZ = activeRotZ
                        },
                    contentScale = ContentScale.Crop
                )

                // Top right overlay: Aspect Ratio label
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(project.aspectRatio, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Center glowing Play overlay if paused
                if (!isPlaying) {
                    IconButton(
                        onClick = { isPlaying = true },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Rounded.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            // Player control action strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play Toggle",
                        tint = PrimaryColor
                    )
                }

                LinearProgressIndicator(
                    progress = { if (isPlaying) progressAnim else 0f },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                    color = PrimaryColor,
                    trackColor = dividerColor
                )

                Text(
                    text = "${(if (isPlaying) progressAnim * project.durationSeconds else 0f).toInt()}s / ${project.durationSeconds}s",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )

                IconButton(onClick = { showFullscreen = true }) {
                    Icon(Icons.Rounded.Fullscreen, "Fullscreen", tint = textPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action CTAs (Download, Share, Re-generate)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Video exported in 4K resolution!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Download, "Download")
                        Text("Download Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        // Share intent simulation
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Look at this amazing AI video I generated on Motion.ai! Prompt: ${project.prompt}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isDarkMode) Color(0xFF333E60) else Color(0xFFCBD5E1))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Share, "Share", tint = textPrimary)
                        Text("Share Link", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Director's Logs & AI Analysis Section (Gemini)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryColor.copy(alpha = 0.05f))
                    .border(BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.15f)), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MovieFilter,
                        contentDescription = "Gemini",
                        tint = SecondaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Director's Cinematic AI Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }

                Text(
                    text = project.geminiDescription,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = textPrimary.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Divider(color = PrimaryColor.copy(alpha = 0.12f))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textPrimary)) {
                            append("Prompt Parameters: ")
                        }
                        append(project.prompt)
                        if (project.negativePrompt.isNotEmpty()) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textPrimary)) {
                                append("\nNegative Parameters: ")
                            }
                            append(project.negativePrompt)
                        }
                    },
                    fontSize = 11.sp,
                    color = textSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. PRICING SCREEN - HIGH-LIGHTED CARDS
// -------------------------------------------------------------
@Composable
fun PricingScreen(
    viewModel: MotionViewModel,
    isDarkMode: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWideScreen: Boolean
) {
    val isProUser by viewModel.isProUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("pricing_screen_container")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Flexible Pricing Plans",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Select a package and upgrade to generate premium watermark-free 4K AI videos.",
                fontSize = 13.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 450.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val plans = listOf(
            PlanData(
                title = "Free",
                price = "${'$'}0",
                period = "/mo",
                badge = "",
                features = listOf("Limited 5 generations / mo", "Standard SD quality", "Includes Watermark", "Access 5s camera zooms"),
                cta = "Get Started",
                isPopular = false,
                color = textSecondary
            ),
            PlanData(
                title = "Pro",
                price = "${'$'}29",
                period = "/mo",
                badge = "MOST POPULAR",
                features = listOf("Unlimited fast generations", "High Definition upscaled outputs", "No Watermark whatsoever", "Access Zoom, Pan, Orbit, Dolly (15s)", "Priority cloud rendering"),
                cta = if (isProUser) "Active Plan" else "Upgrade to Pro",
                isPopular = true,
                color = PrimaryColor
            ),
            PlanData(
                title = "Business",
                price = "${'$'}99",
                period = "/mo",
                badge = "ENTERPRISE",
                features = listOf("Unlimited everything", "Lossless 4K cinematic export", "Custom team workspace (5 members)", "Dedicated REST API key access", "24/7 priority support channel"),
                cta = "Contact Sales",
                isPopular = false,
                color = SecondaryColor
            )
        )

        if (isWideScreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                plans.forEach { plan ->
                    Box(modifier = Modifier.weight(1f)) {
                        PricingPlanCard(
                            plan = plan,
                            cardBg = cardBg,
                            isDarkMode = isDarkMode,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onUpgrade = {
                                if (plan.isPopular) {
                                    viewModel.simulateUpgrade()
                                    Toast.makeText(context, "Successfully upgraded to Pro Plan!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Contact support for custom setup.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                plans.forEach { plan ->
                    PricingPlanCard(
                        plan = plan,
                        cardBg = cardBg,
                        isDarkMode = isDarkMode,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onUpgrade = {
                            if (plan.isPopular) {
                                viewModel.simulateUpgrade()
                                Toast.makeText(context, "Successfully upgraded to Pro Plan!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Action triggered: ${plan.title} plan.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trust badge bottom banner
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryColor.copy(alpha = 0.05f))
                .border(1.dp, PrimaryColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Security, "SSL Secure", tint = SuccessColor, modifier = Modifier.size(18.dp))
                Text("SSL Secure 256-Bit Encrypted Payments", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Cancel anytime with a single click. 14-day full satisfaction money-back guarantee.",
                fontSize = 11.sp,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------------------------------------------
// 5. SETTINGS / AUTHENTICATION SCREEN - LOGIN & SIGN UP FORMS
// -------------------------------------------------------------
@Composable
fun SettingsAuthScreen(
    viewModel: MotionViewModel,
    isDarkMode: Boolean,
    cardBg: Color,
    dividerColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isWideScreen: Boolean
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isProUser by viewModel.isProUser.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    var isRegisterState by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn) {
            // Profile Screen
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(1.dp, dividerColor, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .widthIn(max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userEmail.take(2).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(userEmail, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Text(if (isProUser) "Pro Subscription Active" else "Free Account Tier", fontSize = 13.sp, color = textSecondary)
                }

                Divider(color = dividerColor)

                // Sub Info List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileItemRow("Current Model Version", "v3.5 Turbo (Veo-driven)", textPrimary, textSecondary)
                    ProfileItemRow("Generations Left", if (isProUser) "Unlimited" else "5 remaining", textPrimary, textSecondary)
                    ProfileItemRow("Export Quality Limit", if (isProUser) "4K Cinematic (Lossless)" else "Standard Definition (SD)", textPrimary, textSecondary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.logout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Authentication Card (Login / Register Toggle)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(1.dp, dividerColor, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .widthIn(max = 400.dp)
                    .testTag("auth_card"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isRegisterState) "Create an Account" else "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = if (isRegisterState) "Join Motion.ai and start converting images today" else "Log in to your professional AI film generator studio",
                        fontSize = 11.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // Google Login Button (Highly requested!)
                OutlinedButton(
                    onClick = {
                        viewModel.performAuth("google.creator@gmail.com", false)
                        Toast.makeText(context, "Logged in via Google secure gateway", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, dividerColor)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA4335))
                        )
                        Text("Continue with Google", color = textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Divider(modifier = Modifier.weight(1f), color = dividerColor)
                    Text("OR", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp), color = textSecondary)
                    Divider(modifier = Modifier.weight(1f), color = dividerColor)
                }

                // Email Form
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email address") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (emailInput.isNotEmpty() && passwordInput.length >= 6) {
                            viewModel.performAuth(emailInput, isRegisterState)
                            Toast.makeText(context, "Authenticated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter a valid email and 6-char password", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("auth_submit_button")
                ) {
                    Text(
                        text = if (isRegisterState) "Sign Up" else "Log In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isRegisterState) "Already have an account? " else "New to Motion.ai? ",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                    Text(
                        text = if (isRegisterState) "Log In" else "Sign Up",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.clickable { isRegisterState = !isRegisterState }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileItemRow(label: String, valStr: String, textPrimary: Color, textSecondary: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textSecondary)
        Text(valStr, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
    }
}

// -------------------------------------------------------------
// REUSABLE HELPER VIEW COMPONENTS
// -------------------------------------------------------------
@Composable
fun PresetDemoCard(
    preset: PresetDemo,
    cardBg: Color,
    isDarkMode: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7), RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    ) {
        AsyncImage(
            model = preset.imageUrl,
            contentDescription = preset.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                preset.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                preset.description,
                fontSize = 10.sp,
                color = textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryColor.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("Try Demo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
        }
    }
}

data class FeatureData(val icon: ImageVector, val title: String, val desc: String)

@Composable
fun FeatureCard(feature: FeatureData, cardBg: Color, dividerColor: Color, textPrimary: Color, textSecondary: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, dividerColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryColor.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = feature.icon, contentDescription = feature.title, tint = PrimaryColor, modifier = Modifier.size(20.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(feature.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(feature.desc, fontSize = 11.sp, color = textSecondary, lineHeight = 15.sp)
        }
    }
}

data class TestimonialData(val name: String, val role: String, val text: String, val stars: Int, val initials: String)

@Composable
fun FaqAccordion(
    question: String,
    answer: String,
    cardBg: Color,
    dividerColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(1.dp, dividerColor, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(14.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                question,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.weight(0.9f)
            )
            Icon(
                imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                contentDescription = "Expand",
                tint = textSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                answer,
                fontSize = 12.sp,
                color = textSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

data class PlanData(
    val title: String,
    val price: String,
    val period: String,
    val badge: String,
    val features: List<String>,
    val cta: String,
    val isPopular: Boolean,
    val color: Color
)

@Composable
fun PricingPlanCard(
    plan: PlanData,
    cardBg: Color,
    isDarkMode: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onUpgrade: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg)
            .border(
                border = BorderStroke(
                    width = if (plan.isPopular) 2.dp else 1.dp,
                    color = if (plan.isPopular) PrimaryColor else if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        if (plan.badge.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Brush.linearGradient(colors = listOf(PrimaryColor, SecondaryColor)))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = plan.badge,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Text(plan.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(plan.price, fontSize = 28.sp, fontWeight = FontWeight.Black, color = textPrimary)
            Text(plan.period, fontSize = 12.sp, color = textSecondary, modifier = Modifier.padding(bottom = 4.dp))
        }

        Divider(color = if (isDarkMode) Color(0xFF222B45) else Color(0xFFEDF2F7), modifier = Modifier.padding(vertical = 12.dp))

        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            plan.features.forEach { ft ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Included",
                        tint = plan.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(ft, fontSize = 12.sp, color = textPrimary.copy(alpha = 0.9f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onUpgrade,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (plan.isPopular) PrimaryColor else if (isDarkMode) Color(0xFF1E2638) else Color(0xFFF3F4F6),
                contentColor = if (plan.isPopular) Color.White else textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(plan.cta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
