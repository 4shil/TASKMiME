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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    var sleepTimerEnabled by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableStateOf(30) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

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

            // Theme (placeholder)
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { Text("System default") },
                leadingContent = { Icon(Icons.Default.Palette, null) }
            )

            HorizontalDivider()

            // About
            ListItem(
                headlineContent = { Text("About") },
                supportingContent = { Text("Musicya v1.0") },
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
}
