package com.fourshil.musicya.ui.dsp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fourshil.musicya.audiofx.EqualizerBand
import com.fourshil.musicya.ui.theme.Green400
import com.fourshil.musicya.ui.theme.Slate900
import kotlin.math.roundToInt

@Composable
fun DspScreen(
    viewModel: DspViewModel = viewModel(),
    onBack: () -> Unit
) {
    val bassBoost by viewModel.bassBoost.collectAsState()
    val virtualizer by viewModel.virtualizer.collectAsState()
    val bands by viewModel.equalizerBands.collectAsState()
    val enabled by viewModel.equalizerEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Text(
                "Audio Effects",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = enabled,
                onCheckedChange = { viewModel.setEqualizerEnabled(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Green400)
            )
        }

        // Bass & Virtualizer Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EffectCard(
                title = "Bass Boost",
                value = bassBoost,
                onValueChange = { viewModel.setBassBoost(it) },
                modifier = Modifier.weight(1f)
            )
            EffectCard(
                title = "Virtualizer",
                value = virtualizer,
                onValueChange = { viewModel.setVirtualizer(it) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Equalizer
        Text(
            "Equalizer",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (bands.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                items(bands) { band ->
                    EqBandSlider(
                        band = band,
                        onLevelChange = { level -> viewModel.setBandLevel(band.index, level) }
                    )
                }
            }
        } else {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("Equalizer not available", color = Color.Gray)
            }
        }
    }
}

@Composable
fun EffectCard(
    title: String,
    value: Short,
    onValueChange: (Short) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            // Circular Knob simulation (using simple slider for now)
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt().toShort()) },
                valueRange = 0f..1000f,
                colors = SliderDefaults.colors(thumbColor = Green400, activeTrackColor = Green400)
            )
            Text("${(value / 10)}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EqBandSlider(band: EqualizerBand, onLevelChange: (Short) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp).fillMaxHeight()
    ) {
        // Vertical Slider (Rotated)
        // Android EQ levels are typically -1500 to 1500 mB (-15dB to +15dB)
        // We'll assume range -1500..1500
        
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            // We use layout modifier to allow the rotated component to take proper space or just visual rotation
            // Simple rotation:
            Slider(
                value = band.level.toFloat(),
                onValueChange = { onLevelChange(it.toInt().toShort()) },
                valueRange = -1500f..1500f,
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = 270f
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxWidth,
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(-placeable.width / 2 + placeable.height / 2, -placeable.height / 2 + placeable.width / 2)
                        }
                    }
                    .width(200.dp), // Height of the slider area
                colors = SliderDefaults.colors(thumbColor = Green400, activeTrackColor = Green400)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "${band.centerFreq / 1000}Hz",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "${band.level / 100}dB",
            style = MaterialTheme.typography.labelSmall,
            color = Green400,
            textAlign = TextAlign.Center
        )
    }
}

// Helper for graphicsLayer
fun Modifier.graphicsLayer(block: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit): Modifier =
    this.then(androidx.compose.ui.graphics.graphicsLayer(block))
