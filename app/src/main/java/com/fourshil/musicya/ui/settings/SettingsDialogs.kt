package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fourshil.musicya.data.ThemeMode
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate900

@Composable
fun NeoDialogWrapper(
    title: String,
    onDismiss: () -> Unit,
    contentColor: Color = Slate900,
    surfaceColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(NeoDimens.BorderMedium, contentColor)
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

@Composable
fun SleepTimerDialog(
    sleepTimerActive: Boolean,
    sleepTimerMinutes: Int,
    contentColor: Color,
    surfaceColor: Color,
    onDismiss: () -> Unit,
    onSetTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit
) {
    NeoDialogWrapper(
        title = "SLEEP TIMER",
        onDismiss = onDismiss,
        contentColor = contentColor,
        surfaceColor = surfaceColor
    ) {
         Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 10, 15, 30, 45, 60).forEach { minutes ->
                NeoSelectionItem(
                    text = if (minutes == 60) "1 HOUR" else "$minutes MINUTES",
                    selected = sleepTimerActive && sleepTimerMinutes == minutes,
                    contentColor = contentColor,
                    surfaceColor = surfaceColor,
                    onClick = {
                        onSetTimer(minutes)
                        onDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
             ArtisticButton(
                onClick = { 
                    onCancelTimer()
                    onDismiss() 
                },
                text = if (sleepTimerActive) "CANCEL TIMER" else "CLOSE",
                backgroundColor = contentColor,
                contentColor = surfaceColor,
                 modifier = Modifier.fillMaxWidth()
            )
         }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    contentColor: Color,
    surfaceColor: Color,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit
) {
    NeoDialogWrapper(
        title = "SELECT THEME",
        onDismiss = onDismiss,
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
                        onThemeSelected(mode)
                        onDismiss()
                    }
                )
            }
         }
    }
}

@Composable
fun CrossfadeDialog(
    currentDuration: Int,
    contentColor: Color,
    surfaceColor: Color,
    onDismiss: () -> Unit,
    onDurationSelected: (Int) -> Unit
) {
    NeoDialogWrapper(
        title = "CROSSFADE",
        onDismiss = onDismiss,
        contentColor = contentColor,
        surfaceColor = surfaceColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(0, 2, 5, 8, 10, 12).forEach { seconds ->
                NeoSelectionItem(
                    text = if (seconds == 0) "OFF" else "$seconds SECONDS",
                    selected = currentDuration == seconds,
                    contentColor = contentColor,
                    surfaceColor = surfaceColor,
                    onClick = {
                        onDurationSelected(seconds)
                        onDismiss()
                    }
                )
            }
        }
    }
}
