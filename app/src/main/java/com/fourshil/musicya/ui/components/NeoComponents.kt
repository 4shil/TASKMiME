package com.fourshil.musicya.ui.components
import androidx.compose.foundation.background
import com.fourshil.musicya.ui.theme.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check

/**
 * Soft Neo-Brutalism Design System with Claude Palette
 * 
 * Key Principles:
 * - Softer shadows (with transparency) instead of hard black shadows
 * - Thinner, consistent borders (1.5dp default, 2dp emphasis)
 * - Warm color palette based on Claude Orange (#D97757)
 * - Friendly, approachable aesthetic while maintaining bold typography
 */

// Soft shadow color with transparency for gentle depth
private val CurrentNeoBorder: Color
    @Composable
    get() = if (isSystemInDarkTheme()) SoftBorderDark else SoftBorderLight

private val CurrentNeoShadow: Color
    @Composable
    get() = if (isSystemInDarkTheme()) SoftShadowDark else SoftShadowLight

/**
 * NeoScaffold
 * A wrapper around Scaffold that enforces the Neo design tokens.
 */
@Composable
fun NeoScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}

/**
 * NeoCard - Soft Neo-Brutalist Card
 * Features softer shadows with transparency, thinner borders, friendly corners
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = CurrentNeoBorder,
    borderWidth: Dp = NeoDimens.BorderDefault,
    shadowSize: Dp = NeoDimens.ShadowDefault,
    shape: Shape = RoundedCornerShape(NeoDimens.CornerMedium),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .padding(bottom = shadowSize, end = shadowSize)
    ) {
        // Soft Shadow with transparency
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowSize, y = shadowSize)
                .background(CurrentNeoShadow, shape)
        )

        // Card Surface
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, borderColor, shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            onClick = onClick,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    } else {
                        Modifier
                    }
                ),
            content = content
        )
    }
}


/**
 * Soft Neo-Brutalist Button
 * Characteristics: 
 * - Thinner border (2dp instead of 4dp)
 * - Soft shadow with transparency
 * - Click animation (press down)
 * - Claude Orange primary color
 */
@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RoundedCornerShape(NeoDimens.CornerMedium),
    borderWidth: Dp = NeoDimens.BorderBold,
    shadowSize: Dp = NeoDimens.ShadowDefault,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Offset logic: when pressed, shadow disappears (button moves down-right)
    val translationX = if (isPressed) shadowSize else 0.dp
    val translationY = if (isPressed) shadowSize else 0.dp
    val shadowAlpha = if (isPressed) 0f else 1f

    Box(
        modifier = modifier
            .padding(bottom = shadowSize, end = shadowSize)
    ) {
        // Soft Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowSize, y = shadowSize)
                .background(CurrentNeoShadow, shape)
                .graphicsLayer { alpha = shadowAlpha }
        )
        
        // Button Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = translationX, y = translationY)
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, CurrentNeoBorder, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * Soft Neo-Brutalist Progress Bar
 * Thinner border, softer shadow, Claude Orange fill
 */
@Composable
fun NeoProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val shadowSize = NeoDimens.ShadowSubtle
    val borderColor = CurrentNeoBorder
    
    Box(
        modifier = modifier
            .padding(bottom = shadowSize, end = shadowSize)
    ) {
        // Soft Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(x = shadowSize, y = shadowSize)
                .background(CurrentNeoShadow, RoundedCornerShape(NeoDimens.CornerFull))
        )

        // Main Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(NeoDimens.CornerFull))
                .background(backgroundColor)
                .border(NeoDimens.BorderDefault, borderColor, RoundedCornerShape(NeoDimens.CornerFull))
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(fillColor)
                    .drawBehind {
                        val strokeWidth = NeoDimens.BorderDefault.toPx()
                        drawLine(
                            color = borderColor,
                            start = androidx.compose.ui.geometry.Offset(size.width, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
            )
        }
    }
}

/**
 * Soft Neo-Brutalist Dialog Wrapper
 * Softer shadow, thinner border, friendly corners
 */
@Composable
fun NeoDialogWrapper(
    title: String,
    onDismiss: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = surfaceColor,
            borderColor = CurrentNeoBorder,
            borderWidth = NeoDimens.BorderDefault,
            shadowSize = NeoDimens.ShadowProminent,
            shape = RoundedCornerShape(NeoDimens.CornerLarge)
        ) {
            Column(
                modifier = Modifier.padding(NeoDimens.SpacingXL)
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
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(NeoDimens.TouchTargetMin)
                            .border(NeoDimens.BorderDefault, CurrentNeoBorder, androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Close,
                            contentDescription = "Close dialog",
                            tint = contentColor,
                            modifier = Modifier.size(NeoDimens.IconMedium)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = NeoDimens.SpacingL),
                    thickness = NeoDimens.BorderSubtle,
                    color = CurrentNeoBorder
                )

                content()
            }
        }
    }
}

/**
 * Soft Neo-Brutalist Selection Item
 * Friendly border, proper touch targets, accessible
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
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else surfaceColor
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else contentColor
    val borderColor = CurrentNeoBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NeoDimens.SpacingXS)
            .clip(RoundedCornerShape(NeoDimens.CornerSmall))
            .background(backgroundColor)
            .border(NeoDimens.BorderDefault, borderColor, RoundedCornerShape(NeoDimens.CornerSmall))
            .clickable(onClick = onClick)
            .padding(NeoDimens.SpacingL)
            .heightIn(min = NeoDimens.TouchTargetMin)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
            if (selected) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = textColor,
                    modifier = Modifier.size(NeoDimens.IconMedium)
                )
            }
        }
    }
}
