package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.ThemeMode
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.ui.components.MinimalIconButton
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Clean Minimalistic Settings Screen
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    playerController: PlayerController,
    onBack: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCrossfadeDialog by remember { mutableStateOf(false) }
    
    val currentTheme by viewModel.themeMode.collectAsState()
    val sleepTimerRemaining by playerController.sleepTimerRemaining.collectAsState()
    val crossfadeDuration by viewModel.crossfadeDuration.collectAsState()
    val currentSpeed by playerController.playbackSpeed.collectAsState()
    val scrollState = rememberScrollState()
    
    val sleepTimerMinutes = (sleepTimerRemaining / 60000).toInt()
    val sleepTimerActive = sleepTimerRemaining > 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Clean header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MinimalIconButton(
                    onClick = onBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
                Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = NeoDimens.ScreenPadding)
        ) {
            // Audio Section
            SettingsSection(title = "Audio") {
                SettingsItem(
                    title = "Equalizer",
                    subtitle = "Adjust audio frequencies",
                    icon = Icons.Default.GraphicEq,
                    onClick = onEqualizerClick
                )
                
                SettingsItem(
                    title = "Playback Speed",
                    subtitle = if (currentSpeed == 1.0f) "Normal" else String.format("%.2fx", currentSpeed),
                    icon = Icons.Default.Speed,
                    onClick = { playerController.cyclePlaybackSpeed() },
                    trailingContent = {
                        Text(
                            text = String.format("%.1fx", currentSpeed),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (currentSpeed != 1.0f) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
                
                SettingsItem(
                    title = "Crossfade",
                    subtitle = if (crossfadeDuration == 0) "Off" else "$crossfadeDuration seconds",
                    icon = Icons.Default.SwapHoriz,
                    onClick = { showCrossfadeDialog = true },
                    trailingContent = if (crossfadeDuration > 0) {
                        {
                            Text(
                                text = "${crossfadeDuration}s",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))

            // Preferences Section
            SettingsSection(title = "Preferences") {
                SettingsItem(
                    title = "Sleep Timer",
                    subtitle = if (sleepTimerActive) "$sleepTimerMinutes min remaining" else "Off",
                    icon = Icons.Default.Timer,
                    onClick = { showSleepTimerDialog = true },
                    trailingContent = if (sleepTimerActive) {
                        {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Timer active",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else null
                )
                
                SettingsItem(
                    title = "Theme",
                    subtitle = when (currentTheme) {
                        ThemeMode.SYSTEM -> "System default"
                        ThemeMode.LIGHT -> "Light mode"
                        ThemeMode.DARK -> "Dark mode"
                    },
                    icon = Icons.Default.Palette,
                    onClick = { showThemeDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))

            // Info Section
            SettingsSection(title = "About") {
                SettingsItem(
                    title = "About Musicya",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Default.Info,
                    onClick = {}
                )
            }
            
            Spacer(modifier = Modifier.height(NeoDimens.SpacingHuge))
        }
    }

    // Dialogs
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            sleepTimerActive = sleepTimerActive,
            sleepTimerMinutes = sleepTimerMinutes,
            contentColor = MaterialTheme.colorScheme.onSurface,
            surfaceColor = MaterialTheme.colorScheme.surface,
            onDismiss = { showSleepTimerDialog = false },
            onSetTimer = { playerController.setSleepTimer(it) },
            onCancelTimer = { playerController.cancelSleepTimer() }
        )
    }
    
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            contentColor = MaterialTheme.colorScheme.onSurface,
            surfaceColor = MaterialTheme.colorScheme.surface,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { viewModel.setThemeMode(it) }
        )
    }
    
    if (showCrossfadeDialog) {
        CrossfadeDialog(
            currentDuration = crossfadeDuration,
            contentColor = MaterialTheme.colorScheme.onSurface,
            surfaceColor = MaterialTheme.colorScheme.surface,
            onDismiss = { showCrossfadeDialog = false },
            onDurationSelected = { duration ->
                viewModel.setCrossfadeDuration(duration)
                playerController.setCrossfadeDuration(duration)
            }
        )
    }
}

/**
 * Settings Section with title
 */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = NeoDimens.SpacingM)
        )
        Surface(
            shape = RoundedCornerShape(NeoDimens.CornerLarge),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = NeoDimens.ElevationLow
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * Clean Settings Item
 */
@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(NeoDimens.SpacingL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(NeoDimens.SpacingL))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        trailingContent?.invoke()
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
