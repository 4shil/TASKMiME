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

    NeoScaffold(
        containerColor = NeoBackground,
        topBar = {
            // Clean header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    shadowSize = 4.dp
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = (-1).sp
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // Audio Section
            SettingsSection(title = "AUDIO") {
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
                            color = if (currentSpeed != 1.0f) NeoBlue 
                                    else Color.Gray,
                            fontWeight = FontWeight.Bold
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
                                color = NeoBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences Section
            SettingsSection(title = "PREFERENCES") {
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
                                tint = NeoGreen,
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

            Spacer(modifier = Modifier.height(24.dp))

            // Info Section
            SettingsSection(title = "ABOUT") {
                SettingsItem(
                    title = "About Musicya",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Default.Info,
                    onClick = {}
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Dialogs
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            sleepTimerActive = sleepTimerActive,
            sleepTimerMinutes = sleepTimerMinutes,
            contentColor = Color.Black,
            surfaceColor = Color.White,
            onDismiss = { showSleepTimerDialog = false },
            onSetTimer = { playerController.setSleepTimer(it) },
            onCancelTimer = { playerController.cancelSleepTimer() }
        )
    }
    
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            contentColor = Color.Black,
            surfaceColor = Color.White,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { viewModel.setThemeMode(it) }
        )
    }
    
    if (showCrossfadeDialog) {
        CrossfadeDialog(
            currentDuration = crossfadeDuration,
            contentColor = Color.Black,
            surfaceColor = Color.White,
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
            color = Color.Black,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            letterSpacing = 1.sp
        )
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White,
            shadowSize = 4.dp,
            shape = RoundedCornerShape(16.dp),
            borderWidth = 2.dp
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
