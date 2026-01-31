package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.NeoPrimary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Neo-Brutalist Dialog showing detailed information about a song.
 */
@Composable
fun SongDetailsDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            // backgroundColor = MaterialTheme.colorScheme.surface, // Default is correct now
            shadowSize = NeoDimens.ShadowProminent,
            shape = RoundedCornerShape(NeoDimens.CornerLarge)
        ) {
            Column(
                modifier = Modifier
                    .padding(NeoDimens.SpacingL)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Song Details",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = NeoDimens.SpacingL)
                )

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f, fill = false) // Allow scrolling if too long
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(NeoDimens.SpacingM)
                ) {
                    DetailRow("TITLE", song.title)
                    DetailRow("ARTIST", song.artist)
                    DetailRow("ALBUM", song.album)
                    DetailRow("DURATION", song.durationFormatted)
                    DetailRow("SIZE", formatFileSize(song.size))
                    DetailRow("PATH", song.path, isPath = true)
                    DetailRow("DATE ADDED", formatDate(song.dateAdded))
                }

                Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))

                // Close Button
                NeoButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp),
                    shadowSize = 4.dp
                ) {
                    Text(
                        text = "CLOSE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, isPath: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label with decorative line
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy( // Larger label
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Value
        Text(
            text = value.ifEmpty { "Unknown" },
            style = if (isPath) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium, // Larger Value
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface, // High contrast
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp * 1000)) // timestamp is in seconds
    } catch (e: Exception) {
        "Unknown"
    }
}
