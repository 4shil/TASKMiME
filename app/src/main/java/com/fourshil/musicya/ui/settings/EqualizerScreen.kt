package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.player.EqBand
import com.fourshil.musicya.ui.components.MinimalIconButton
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.components.NeoScaffold
import com.fourshil.musicya.ui.components.NeoCard
import com.fourshil.musicya.ui.theme.NeoBackground

/**
 * Complete Equalizer Screen with:
 * - 5-Band EQ with vertical sliders
 * - Presets dropdown
 * - Bass Boost slider
 * - Virtualizer slider
 * - Loudness Enhancer slider
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val isEnabled by viewModel.isEnabled.collectAsState()
    val bands by viewModel.bands.collectAsState()
    val bassLevel by viewModel.bassLevel.collectAsState()
    val virtualizerLevel by viewModel.virtualizerLevel.collectAsState()
    val loudnessLevel by viewModel.loudnessLevel.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val currentPreset by viewModel.currentPreset.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState()

    val isInitialized by viewModel.isInitialized.collectAsState()
 
    NeoScaffold(
        containerColor = NeoBackground,
        topBar = {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MinimalIconButton(
                        onClick = onBack,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                    Spacer(modifier = Modifier.width(NeoDimens.SpacingM))
                    Text(
                        text = "Equalizer",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Enable/Disable Switch
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { viewModel.toggleEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = NeoDimens.ScreenPadding)
        ) {
            if (!isInitialized) {
                // Not initialized state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
                        Text(
                            "Initializing audio engine...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Preset Selector
                if (presets.isNotEmpty()) {
                    PresetSelector(
                        presets = presets,
                        currentPreset = currentPreset,
                        enabled = isEnabled,
                        onPresetSelected = { viewModel.setPreset(it) },
                        onReset = { viewModel.resetEqualizer() }
                    )
                    Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
                }
                
                // Equalizer Bands
                if (bands.isNotEmpty()) {
                    EqBandsSection(
                        bands = bands,
                        enabled = isEnabled,
                        onBandChange = { index, level -> viewModel.setBandLevel(index, level) }
                    )
                    Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
                }
                
                // FX Controls Section
                FxControlsSection(
                    bassLevel = bassLevel,
                    virtualizerLevel = virtualizerLevel,
                    loudnessLevel = loudnessLevel,
                    enabled = isEnabled,
                    onBassChange = { viewModel.setBassLevel(it) },
                    onVirtualizerChange = { viewModel.setVirtualizerLevel(it) },
                    onLoudnessChange = { viewModel.setLoudnessLevel(it) }
                )
                
                Spacer(modifier = Modifier.height(NeoDimens.SpacingHuge))
            }
        }
    }
}

@Composable
private fun PresetSelector(
    presets: List<String>,
    currentPreset: Int,
    enabled: Boolean,
    onPresetSelected: (Int) -> Unit,
    onReset: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val presetName = if (currentPreset >= 0 && currentPreset < presets.size) {
        presets[currentPreset]
    } else {
        "Custom"
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
    ) {
        // Preset Dropdown
        Box(modifier = Modifier.weight(1f)) {
            NeoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Height adjustment for shadow
                backgroundColor = Color.White,
                shadowSize = 4.dp,
                onClick = { if (enabled) expanded = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = NeoDimens.SpacingL),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = presetName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "▼",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                presets.forEachIndexed { index, preset ->
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = preset,
                                fontWeight = if (index == currentPreset) FontWeight.Bold else FontWeight.Normal,
                                color = if (index == currentPreset) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onPresetSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Reset Button
        MinimalIconButton(
            onClick = onReset,
            icon = Icons.Default.Refresh,
            contentDescription = "Reset to flat",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun EqBandsSection(
    bands: List<EqBand>,
    enabled: Boolean,
    onBandChange: (Int, Int) -> Unit
) {
    onBandChange: (Int, Int) -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White,
        shadowSize = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(NeoDimens.SpacingL)
        ) {
            // Level indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = NeoDimens.SpacingS),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "+15dB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "0dB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "-15dB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Sliders
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bands.forEach { band ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Vertical Slider
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Slider(
                                value = band.level.toFloat(),
                                onValueChange = { onBandChange(band.index, it.toInt()) },
                                valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                                enabled = enabled,
                                modifier = Modifier
                                    .graphicsLayer { rotationZ = 270f }
                                    .width(160.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledThumbColor = MaterialTheme.colorScheme.outline,
                                    disabledActiveTrackColor = MaterialTheme.colorScheme.outline,
                                    disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                        
                        // Frequency Label
                        Text(
                            text = band.formatFrequency(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FxControlsSection(
    bassLevel: Int,
    virtualizerLevel: Int,
    loudnessLevel: Int,
    enabled: Boolean,
    onBassChange: (Int) -> Unit,
    onVirtualizerChange: (Int) -> Unit,
    onLoudnessChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingL)) {
        Text(
            text = "Audio Effects",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        
        )
        
        NeoCard(
            backgroundColor = Color.White,
            shadowSize = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(NeoDimens.SpacingL),
                verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingL)
            ) {
                FxSlider(
                    label = "Bass Boost",
                    value = bassLevel,
                    maxValue = 1000,
                    enabled = enabled,
                    formatValue = { "${(it / 10)}%" },
                    onValueChange = onBassChange
                )
                
                FxSlider(
                    label = "Virtualizer",
                    value = virtualizerLevel,
                    maxValue = 1000,
                    enabled = enabled,
                    formatValue = { "${(it / 10)}%" },
                    onValueChange = onVirtualizerChange
                )
                
                FxSlider(
                    label = "Volume Boost",
                    value = loudnessLevel,
                    maxValue = 100,
                    enabled = enabled,
                    formatValue = { "+${it / 10}dB" },
                    onValueChange = onLoudnessChange
                )
            }
        }
    }
}

@Composable
private fun FxSlider(
    label: String,
    value: Int,
    maxValue: Int,
    enabled: Boolean,
    formatValue: (Int) -> String,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatValue(value),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (value > 0) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(NeoDimens.SpacingXS))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..maxValue.toFloat(),
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledThumbColor = MaterialTheme.colorScheme.outline,
                disabledActiveTrackColor = MaterialTheme.colorScheme.outline,
                disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
