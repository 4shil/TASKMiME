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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Suppress("DEPRECATION")
import androidx.compose.material.ripple.rememberRipple
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
    showHalftone: Boolean = false, // Disabled by default for cleaner look
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Smooth spring animation for press feedback
    val shadowOffset by animateDpAsState(
        targetValue = if (isPressed) 1.dp else shadowSize,
        label = "shadow",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) (shadowSize - 1.dp) else 0.dp,
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
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
            .padding(bottom = shadowSize, end = shadowSize)
            .then(currentModifier)
    ) {
        // Shadow Layer - Small and clean
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor)
        )
        
        // Main Content Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = pressOffset, y = pressOffset)
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

