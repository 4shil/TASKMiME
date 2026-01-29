package com.fourshil.musicya.ui.components
import androidx.compose.foundation.background
import com.fourshil.musicya.ui.theme.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
    containerColor: Color = NeoBackground,
    contentColor: Color = Color.Black,
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
 * NeoCard
 * A container with a white background, black border, and hard shadow.
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp, // Thinner than buttons typically
    shadowSize: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .padding(bottom = shadowSize, end = shadowSize)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowSize, y = shadowSize)
                .background(Color.Black, shape)
        )

        // Card Surface
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, borderColor, shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    }
                ),
            content = content
        )
    }
}


/**
 * Neobrutalism Button
 * Characteristics: 
 * - Thick black border (4dp)
 * - Hard shadow (offset)
 * - Click animation (press down)
 */
@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 4.dp,
    shadowSize: Dp = 4.dp, // 'neobrutal' shadow
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
            .padding(bottom = shadowSize, end = shadowSize) // Reserve space for shadow
    ) {
        // Shadow Layer (Manual implementation for hard edge)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowSize, y = shadowSize)
                .background(Color.Black, shape)
                .graphicsLayer { alpha = shadowAlpha }
        )
        
        // Button Surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = translationX, y = translationY)
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, Color.Black, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // No ripple, we handle movement
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * Neobrutalism Progress Bar
 */
@Composable
fun NeoProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    height: Dp = 24.dp,
    fillColor: Color = NeoPrimary,
    backgroundColor: Color = Color.White
) {
    // shadow-neobrutal-sm: 2px offset
    val shadowSize = 2.dp
    
    Box(
        modifier = modifier
            .padding(bottom = shadowSize, end = shadowSize) // Space for shadow
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(x = shadowSize, y = shadowSize)
                .background(Color.Black, RoundedCornerShape(50))
        )

        // Main Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(backgroundColor)
                .border(4.dp, Color.Black, RoundedCornerShape(50))
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(fillColor)
                    // The CSS has border-r-4 black on the fill div
                    .drawBehind {
                        val strokeWidth = 4.dp.toPx()
                        drawLine(
                            color = Color.Black,
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
 * Neo-Brutalist Dialog Wrapper
 * Hard shadow, thick border, bold header.
 */
@Composable
fun NeoDialogWrapper(
    title: String,
    onDismiss: () -> Unit,
    contentColor: Color = Color.Black,
    surfaceColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = surfaceColor,
            borderColor = Color.Black,
            borderWidth = 2.dp,
            shadowSize = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = contentColor,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).border(2.dp, Color.Black, androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = Color.Black
                )

                content()
            }
        }
    }
}

/**
 * Neo-Brutalist Selection Item
 * Hard border, bold text, high contrast selection.
 */
@Composable
fun NeoSelectionItem(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.Black,
    surfaceColor: Color = Color.White,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) NeoPrimary else surfaceColor
    val textColor = if (selected) Color.White else contentColor
    val borderColor = Color.Black

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium, // Bold body
                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                color = textColor
            )
            if (selected) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
