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
                .fillMaxSize()
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
