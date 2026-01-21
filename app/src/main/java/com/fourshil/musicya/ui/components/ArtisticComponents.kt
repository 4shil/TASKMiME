@file:Suppress("DEPRECATION")
package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Clean Minimalistic Card Component
 * Legacy wrapper that delegates to Surface-based implementation
 */
@Composable
fun ArtisticCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    shadowColor: Color = MaterialTheme.colorScheme.outline,
    shadowSize: Dp = NeoDimens.ElevationMedium,
    borderWidth: Dp = NeoDimens.BorderThin,
    showHalftone: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        color = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = shadowSize,
        shadowElevation = shadowSize
    ) {
        Box(content = content)
    }
}

/**
 * Clean Minimalistic Button Component
 * With subtle press animation
 */
@Composable
fun ArtisticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    icon: (@Composable () -> Unit)? = null,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    activeContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shadowSize: Dp = NeoDimens.ElevationMedium,
    borderWidth: Dp = NeoDimens.BorderThin
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "buttonScale"
    )

    val containerColor = if (isActive) activeColor else backgroundColor
    val tintColor = if (isActive) activeContentColor else contentColor

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        color = containerColor,
        contentColor = tintColor,
        tonalElevation = NeoDimens.ElevationLow
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(NeoDimens.SpacingM)
        ) {
            icon?.invoke()
        }
    }
}

/**
 * Clean Dialog Wrapper
 */
@Composable
fun NeoDialogWrapper(
    title: String,
    onDismiss: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(NeoDimens.CornerLarge),
            color = surfaceColor,
            tonalElevation = NeoDimens.ElevationHigh,
            shadowElevation = NeoDimens.ElevationHigh
        ) {
            Column(
                modifier = Modifier.padding(NeoDimens.SpacingL)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = NeoDimens.SpacingM),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                content()
            }
        }
    }
}

/**
 * Clean Selection Item for dialogs
 */
@Composable
fun NeoSelectionItem(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        surfaceColor
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(NeoDimens.CornerMedium))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(NeoDimens.CornerMedium),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(NeoDimens.SpacingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else contentColor
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(NeoDimens.IconMedium)
                )
            }
        }
    }
}
