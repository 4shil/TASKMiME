package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.ThemeMode
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
import com.fourshil.musicya.ui.theme.NeoShadowLight

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    playerController: PlayerController,
    onBack: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    val currentTheme by viewModel.themeMode.collectAsState()
    val sleepTimerRemaining by playerController.sleepTimerRemaining.collectAsState()
    val scrollState = rememberScrollState()
    
    // Convert remaining ms to minutes for display
    val sleepTimerMinutes = (sleepTimerRemaining / 60000).toInt()
    val sleepTimerActive = sleepTimerRemaining > 0

    // Determine content color based on theme
    // Use MaterialTheme directly for consistency
    val contentColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize().background(surfaceColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = NeoDimens.ScreenPadding)
        ) {
             Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
            
            // --- HEADER ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                 ArtisticButton(
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back", tint = contentColor) },
                    modifier = Modifier.size(NeoDimens.ButtonHeightMedium), // Was 56.dp
                    backgroundColor = surfaceColor,

                )
                Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.displaySmall.copy( // Was headlineLarge
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- AUDIO SECTION ---
            SettingsSectionHeader("Audio", contentColor)
            SettingsItem(
                title = "Equalizer",
                subtitle = "Adjust audio frequencies",
                icon = Icons.Default.GraphicEq,
                onClick = onEqualizerClick,
                contentColor = contentColor,
                borderColor = contentColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Playback Speed
            val currentSpeed by playerController.playbackSpeed.collectAsState()
            SettingsItem(
                title = "Playback Speed",
                subtitle = if (currentSpeed == 1.0f) "Normal" else String.format("%.2fx", currentSpeed),
                icon = Icons.Default.Speed,
                onClick = { playerController.cyclePlaybackSpeed() },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = {
                    Text(
                        text = String.format("%.1fx", currentSpeed),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = if (currentSpeed != 1.0f) NeoCoral else contentColor.copy(alpha = 0.6f)
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Crossfade
            var showCrossfadeDialog by remember { mutableStateOf(false) }
            val crossfadeDuration by viewModel.crossfadeDuration.collectAsState()
            SettingsItem(
                title = "Crossfade",
                subtitle = if (crossfadeDuration == 0) "Off" else "$crossfadeDuration seconds",
                icon = Icons.Default.SwapHoriz,
                onClick = { showCrossfadeDialog = true },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = if (crossfadeDuration > 0) {
                    {
                        Text(
                            text = "${crossfadeDuration}s",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = NeoCoral
                        )
                    }
                } else null
            )
            
            // Crossfade Dialog
            if (showCrossfadeDialog) {
                CrossfadeDialog(
                    currentDuration = crossfadeDuration,
                    contentColor = contentColor,
                    surfaceColor = surfaceColor,
                    onDismiss = { showCrossfadeDialog = false },
                    onDurationSelected = { duration -> viewModel.setCrossfadeDuration(duration) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PREFERENCES SECTION ---
            SettingsSectionHeader("Preferences", contentColor)
            
            // Sleep Timer - Connected to PlayerController
            SettingsItem(
                title = "Sleep Timer",
                subtitle = if (sleepTimerActive) "$sleepTimerMinutes min remaining" else "Off",
                icon = Icons.Default.Timer,
                onClick = { showSleepTimerDialog = true },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = if (sleepTimerActive) {
                    {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Timer active",
                            tint = NeoCoral,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Theme
            SettingsItem(
                title = "Theme",
                subtitle = when (currentTheme) {
                    ThemeMode.SYSTEM -> "System default"
                    ThemeMode.LIGHT -> "Light mode"
                    ThemeMode.DARK -> "Dark mode"
                },
                icon = Icons.Default.Palette,
                onClick = { showThemeDialog = true },
                contentColor = contentColor,
                borderColor = contentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- INFO SECTION ---
            SettingsSectionHeader("Info", contentColor)
            SettingsItem(
                title = "About Musicya",
                subtitle = "Version 1.0.0",
                icon = Icons.Default.Info,
                onClick = {},
                contentColor = contentColor,
                borderColor = contentColor
            )
             
             Spacer(modifier = Modifier.height(48.dp))
        }

        // --- CUSTOM DIALOGS ---
        if (showSleepTimerDialog) {
            SleepTimerDialog(
                sleepTimerActive = sleepTimerActive,
                sleepTimerMinutes = sleepTimerMinutes,
                contentColor = contentColor,
                surfaceColor = surfaceColor,
                onDismiss = { showSleepTimerDialog = false },
                onSetTimer = { playerController.setSleepTimer(it) },
                onCancelTimer = { playerController.cancelSleepTimer() }
            )
        }
        
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentTheme = currentTheme,
                contentColor = contentColor,
                surfaceColor = surfaceColor,
                onDismiss = { showThemeDialog = false },
                onThemeSelected = { viewModel.setThemeMode(it) }
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        ),
        color = color.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color = Slate900,
    borderColor: Color = Slate700,
    trailingContent: @Composable (() -> Unit)? = null
) {
    // Invert the colors for the icon box to make it pop
    val iconBgColor = contentColor.copy(alpha = 0.1f)
    
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent, // Transparent because we rely on the main background
        borderColor = borderColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, borderColor)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                 Icon(icon, null, modifier = Modifier.size(24.dp), tint = contentColor)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
            
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

