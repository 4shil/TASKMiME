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
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.theme.PureBlack

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    var sleepTimerEnabled by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableStateOf(30) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    val currentTheme by viewModel.themeMode.collectAsState()
    val scrollState = rememberScrollState()

    // Determine content color based on theme
    val isDark = when (currentTheme) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val contentColor = if (isDark) Color.White else PureBlack
    val surfaceColor = if (isDark) PureBlack else Color.White

    Box(modifier = Modifier.fillMaxSize().background(surfaceColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
             Spacer(modifier = Modifier.height(24.dp))
            
            // --- HEADER ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                 ArtisticButton(
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = contentColor) },
                    modifier = Modifier.size(56.dp),
                    backgroundColor = surfaceColor,

                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                         letterSpacing = (-2).sp
                    ),
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- AUDIO SECTION ---
            SettingsSectionHeader("AUDIO CONTROL", contentColor)
            SettingsItem(
                title = "EQUALIZER",
                subtitle = "ADJUST FREQUENCIES",
                icon = Icons.Default.GraphicEq,
                onClick = onEqualizerClick,
                contentColor = contentColor,
                borderColor = contentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- PREFERENCES SECTION ---
            SettingsSectionHeader("PREFERENCES", contentColor)
            
            // Sleep Timer
             SettingsItem(
                title = "SLEEP TIMER",
                subtitle = if (sleepTimerEnabled) "$sleepTimerMinutes MIN REMAINING" else "DISABLED",
                icon = Icons.Default.Timer,
                onClick = { showSleepTimerDialog = true },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = {
                    Switch(
                        checked = sleepTimerEnabled,
                        onCheckedChange = { sleepTimerEnabled = it },
                         colors = SwitchDefaults.colors(
                            checkedThumbColor = surfaceColor, // Inverted for contrast
                            checkedTrackColor = contentColor,
                            uncheckedThumbColor = contentColor,
                            uncheckedTrackColor = surfaceColor,
                            uncheckedBorderColor = contentColor
                        )
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Theme
            SettingsItem(
                title = "THEME MODE",
                subtitle = when (currentTheme) {
                    ThemeMode.SYSTEM -> "SYSTEM DEFAULT"
                    ThemeMode.LIGHT -> "LIGHT MODE"
                    ThemeMode.DARK -> "DARK MODE"
                },
                icon = Icons.Default.Palette,
                onClick = { showThemeDialog = true },
                contentColor = contentColor,
                borderColor = contentColor
            )

             Spacer(modifier = Modifier.height(24.dp))

            // --- INFO SECTION ---
            SettingsSectionHeader("SYSTEM INFO", contentColor)
            SettingsItem(
                title = "ABOUT LYRA",
                subtitle = "VERSION 1.0.0 // BETA",
                icon = Icons.Default.Info,
                onClick = {},
                contentColor = contentColor,
                borderColor = contentColor
            )
             
             Spacer(modifier = Modifier.height(48.dp))
        }

        // --- CUSTOM DIALOGS ---
        if (showSleepTimerDialog) {
            NeoDialogWrapper(
                title = "SLEEP TIMER",
                onDismiss = { showSleepTimerDialog = false },
                contentColor = contentColor,
                surfaceColor = surfaceColor
            ) {
                 Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 30, 45, 60, 90).forEach { minutes ->
                        NeoSelectionItem(
                            text = "$minutes MINUTES",
                            selected = sleepTimerMinutes == minutes && sleepTimerEnabled,
                            contentColor = contentColor,
                            surfaceColor = surfaceColor,
                            onClick = {
                                sleepTimerMinutes = minutes
                                sleepTimerEnabled = true
                                showSleepTimerDialog = false
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                     ArtisticButton(
                        onClick = { 
                            sleepTimerEnabled = false
                            showSleepTimerDialog = false 
                        },
                        text = "DISABLE TIMER",
                        backgroundColor = contentColor,
                        contentColor = surfaceColor,
                         modifier = Modifier.fillMaxWidth()
                    )
                 }
            }
        }
        
        if (showThemeDialog) {
             NeoDialogWrapper(
                title = "SELECT THEME",
                onDismiss = { showThemeDialog = false },
                contentColor = contentColor,
                surfaceColor = surfaceColor
            ) {
                 Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                         NeoSelectionItem(
                            text = when (mode) {
                                ThemeMode.SYSTEM -> "SYSTEM DEFAULT"
                                ThemeMode.LIGHT -> "LIGHT MODE"
                                ThemeMode.DARK -> "DARK MODE"
                            },
                            selected = currentTheme == mode,
                            contentColor = contentColor,
                            surfaceColor = surfaceColor,
                            onClick = {
                                viewModel.setThemeMode(mode)
                                showThemeDialog = false
                            }
                        )
                    }
                 }
            }
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
    contentColor: Color = PureBlack,
    borderColor: Color = PureBlack,
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

@Composable
fun NeoDialogWrapper(
    title: String,
    onDismiss: () -> Unit,
    contentColor: Color = PureBlack,
    surfaceColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
         Box(
            modifier = Modifier
                .fillMaxWidth()
                 .border(4.dp, contentColor)
                 .background(surfaceColor)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = contentColor
                    )
                    Icon(
                        Icons.Default.Close, 
                        null, 
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .size(24.dp),
                        tint = contentColor
                    )
                }
                HorizontalDivider(
                    thickness = 4.dp, 
                    color = contentColor,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                content()
            }
        }
    }
}

@Composable
fun NeoSelectionItem(
    text: String,
    selected: Boolean,
    contentColor: Color,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    // If selected, we INVERT the colors (Background = ContentColor, Text = SurfaceColor)
    val bgColor = if (selected) contentColor else Color.Transparent
    val textColor = if (selected) surfaceColor else contentColor
    val borderW = if (selected) 2.dp else 0.dp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderW, contentColor)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
             Icon(Icons.Default.Check, null, tint = textColor, modifier = Modifier.size(20.dp))
             Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
             color = textColor
        )
    }
}

