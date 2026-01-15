package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel = hiltViewModel(),
    audioSessionId: Int = 0,
    onBack: () -> Unit = {}
) {
    val isEnabled by viewModel.isEnabled.collectAsState()
    val bands by viewModel.bands.collectAsState()
    val bassLevel by viewModel.bassLevel.collectAsState()
    val virtualizerLevel by viewModel.virtualizerLevel.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val currentPreset by viewModel.currentPreset.collectAsState()

    LaunchedEffect(audioSessionId) {
        if (audioSessionId > 0) {
            viewModel.initialize(audioSessionId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equalizer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { viewModel.toggleEnabled(it) }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Presets Dropdown
            if (presets.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (currentPreset >= 0) presets[currentPreset] else "Custom",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Preset") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        presets.forEachIndexed { index, preset ->
                            DropdownMenuItem(
                                text = { Text(preset) },
                                onClick = {
                                    viewModel.setPreset(index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // EQ Bands
            Text("Equalizer", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bands.forEach { band ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Slider(
                            value = band.level.toFloat(),
                            onValueChange = { viewModel.setBandLevel(band.index, it.toInt()) },
                            valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                            enabled = isEnabled,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                        Text(
                            "${band.centerFreq}Hz",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bass Boost
            Text("Bass Boost", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = bassLevel.toFloat(),
                onValueChange = { viewModel.setBassLevel(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Virtualizer
            Text("Virtualizer", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = virtualizerLevel.toFloat(),
                onValueChange = { viewModel.setVirtualizerLevel(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
