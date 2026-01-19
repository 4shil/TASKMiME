@file:Suppress("DEPRECATION")
package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoShadowLight
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Neo-Brutalism Card Component
 * Features small shadows (3dp), clean borders, and smooth 60fps animations
 */
@Composable
fun ArtisticCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    shadowColor: Color = NeoShadowLight,
    shadowSize: Dp = NeoDimens.ShadowMedium,
    borderWidth: Dp = NeoDimens.BorderThin,
    showHalftone: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animate offset of the MAIN content (shadow stays fixed or shrinks)
    // Actually, in Neo-Brutalism, usually the card moves DOWN to meet the shadow.
    val targetOffset = if (isPressed) shadowSize else 0.dp
    
    // We animate the Translation of the content
    val offsetAnim by animateDpAsState(
        targetValue = targetOffset,
        label = "offset",
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    val currentModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = rememberRipple(bounded = true),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            // Outer container reserves space for the max shadow
            .padding(bottom = shadowSize, end = shadowSize) 
            .then(currentModifier)
            .drawBehind {
                // Draw Shadow at full size (bottom-right)
                // The shadow is static in this simplified version to save perf
                // effectively acting as the "hole" the card falls into
                drawRect(
                    color = shadowColor,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = shadowSize.toPx(), 
                        y = shadowSize.toPx()
                    ),
                    size = size
                )
            }
    ) {
        // Main Content Layer
        Box(
            modifier = Modifier
                .offset(x = offsetAnim, y = offsetAnim)
                // We fake the "movement" by translating. 
                // However, the shadow is drawn by the PARENT. 
                // But wait, if parent draws shadow at (0,0) -> (W,H) shifted by shadowSize...
                // And child translates from (0,0) to (shadowSize, shadowSize)...
                // That creates the press effect.
                 
                // Border and Background
                .fillMaxWidth() // Assuming Card fills available width usually? No, let it wrap content or be defined by parent.
                // Revert fillMaxWidth, rely on matchParentSize logic or propagation
                .border(borderWidth, borderColor)
                .background(backgroundColor)
        ) {
            content()
            if (showHalftone) {
                HalftoneBackground(
                    modifier = Modifier.matchParentSize(),
                    color = borderColor.copy(alpha = 0.03f)
                )
            }
        }
    }
}

/**
 * Neo-Brutalism Button Component
 * Features small shadows (3dp), smooth press animation, and clean typography
 */
@Composable
fun ArtisticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    enabled: Boolean = true,
    text: String? = null,
    icon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    activeColor: Color = Slate900,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    activeContentColor: Color = Color.White,
    shadowSize: Dp = NeoDimens.ShadowMedium,
    borderWidth: Dp = NeoDimens.BorderThin
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val currentBg = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        isActive || isPressed -> activeColor
        else -> backgroundColor
    }
    val currentContent = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        isActive || isPressed -> activeContentColor
        else -> contentColor
    }
    
    // Smooth animation for shadow
    val animatedShadow by animateDpAsState(
        targetValue = if (isPressed || !enabled) 0.dp else shadowSize,
        label = "shadow",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed) shadowSize else 0.dp,
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Box(
        modifier = modifier
            .padding(bottom = shadowSize, end = shadowSize)
            .alpha(if (enabled) 1f else 0.6f)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true),
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = animatedShadow, y = animatedShadow)
                .background(NeoShadowLight)
        )
        
        // Content
        Box(
            modifier = Modifier
                .offset(x = animatedOffset, y = animatedOffset)
                .border(borderWidth, if (enabled) Slate700 else MaterialTheme.colorScheme.outline)
                .background(currentBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (text != null) {
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = currentContent
                )
            }
            if (icon != null) {
                Box(modifier = Modifier.padding(if (text != null) 4.dp else 0.dp)) {
                    icon()
                }
            }
        }
    }
}

/**
 * Manga-style FX text - decorative accent element
 */
@Composable
fun MangaFX(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeoCoral,
    rotation: Float = -12f
) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayMedium.copy(
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Black
        ),
        color = color,
        modifier = modifier.rotate(rotation)
    )
}

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
    modifier: Modifier = Modifier,
    contentColor: Color = Slate900,
    surfaceColor: Color = Color.White,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) contentColor else surfaceColor
    val textColor = if (selected) surfaceColor else contentColor

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(NeoDimens.BorderThin, contentColor)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
