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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.player.EqBand
import com.fourshil.musicya.ui.components.MinimalIconButton
import com.fourshil.musicya.ui.components.NeoCard
import com.fourshil.musicya.ui.components.NeoScaffold
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Equalizer Screen (remade)
 * - Neo-brutalist cards
 * - Tall vertical sliders with large touch targets
 * - Presets + audio effects
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

    NeoScaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
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
                        contentDescription = "Go back",
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(NeoDimens.SpacingM))
                    Text(
                        text = "Equalizer",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
                        Text(
                            text = "Initializing audio engine...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
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

                if (bands.isNotEmpty()) {
                    EqBandsSection(
                        bands = bands,
                        enabled = isEnabled,
                        onBandChange = { index, level -> viewModel.setBandLevel(index, level) }
                    )
                    Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
                }

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
    val presetName = if (currentPreset in presets.indices) presets[currentPreset] else "Custom"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            NeoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shadowSize = 4.dp,
                onClick = { if (enabled) expanded = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = presetName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "▼",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .heightIn(max = 350.dp)
                ) {
                    presets.forEachIndexed { index, preset ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = preset,
                                    fontWeight = if (index == currentPreset) FontWeight.ExtraBold
                                    else FontWeight.Medium,
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
        }

        MinimalIconButton(
            onClick = onReset,
            icon = Icons.Default.Refresh,
            contentDescription = "Reset equalizer",
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
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        shadowSize = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeoDimens.SpacingL)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+15dB",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "0dB",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "-15dB",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(460.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                bands.forEachIndexed { index, band ->
                    EqualizerBandSlider(
                        band = band,
                        enabled = enabled,
                        onValueChange = { level -> onBandChange(index, level) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EqualizerBandSlider(
    band: EqBand,
    enabled: Boolean,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(420.dp)
                .width(72.dp),
            contentAlignment = Alignment.Center
        ) {
            val sliderLength = maxHeight
            Slider(
                value = band.level.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                enabled = enabled,
                modifier = Modifier
                    .graphicsLayer { rotationZ = 270f }
                    .width(sliderLength)
                    .height(52.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledThumbColor = MaterialTheme.colorScheme.outline,
                    disabledActiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledInactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = band.formatFrequency(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )

        NeoCard(shadowSize = 4.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NeoDimens.SpacingL),
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

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                FxSlider(
                    label = "Virtualizer",
                    value = virtualizerLevel,
                    maxValue = 1000,
                    enabled = enabled,
                    formatValue = { "${(it / 10)}%" },
                    onValueChange = onVirtualizerChange
                )

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatValue(value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (value > 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                disabledActiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledInactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}
