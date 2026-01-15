package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Equalizer
            ListItem(
                modifier = Modifier.clickable(onClick = onEqualizerClick),
                headlineContent = { Text("Equalizer") },
                supportingContent = { Text("Adjust audio effects") },
                leadingContent = { Icon(Icons.Default.Equalizer, null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, null) }
            )

            HorizontalDivider()

            // Sleep Timer
            ListItem(
                modifier = Modifier.clickable { showSleepTimerDialog = true },
                headlineContent = { Text("Sleep Timer") },
                supportingContent = { 
                    Text(if (sleepTimerEnabled) "$sleepTimerMinutes min remaining" else "Off") 
                },
                leadingContent = { Icon(Icons.Default.Timer, null) },
                trailingContent = {
                    Switch(
                        checked = sleepTimerEnabled,
                        onCheckedChange = { sleepTimerEnabled = it }
                    )
                }
            )

            HorizontalDivider()

            // Theme
            ListItem(
                modifier = Modifier.clickable { showThemeDialog = true },
                headlineContent = { Text("Theme") },
                supportingContent = { 
                    Text(when (currentTheme) {
                        ThemeMode.SYSTEM -> "System default"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    })
                },
                leadingContent = { Icon(Icons.Default.Palette, null) },
                trailingContent = { Icon(Icons.Default.ChevronRight, null) }
            )

            HorizontalDivider()

            // About
            ListItem(
                headlineContent = { Text("About") },
                supportingContent = { Text("LYRA v1.0") },
                leadingContent = { Icon(Icons.Default.Info, null) }
            )
        }
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("Sleep Timer") },
            text = {
                Column {
                    listOf(15, 30, 45, 60, 90).forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sleepTimerMinutes = minutes
                                    sleepTimerEnabled = true
                                    showSleepTimerDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = sleepTimerMinutes == minutes && sleepTimerEnabled,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$minutes minutes")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    sleepTimerEnabled = false
                    showSleepTimerDialog = false 
                }) {
                    Text("Turn Off")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == mode,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(when (mode) {
                                ThemeMode.SYSTEM -> "System default"
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                            })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

