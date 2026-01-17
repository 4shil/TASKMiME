package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.TileMode.Companion.Repeated



@Composable
fun ArtisticCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = PureBlack,
    shadowColor: Color = PureBlack,
    showHalftone: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val offset by animateFloatAsState(
        targetValue = if (isPressed) 2f else 6f, 
        label = "offset",
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    val shadowOffset by animateFloatAsState(
        targetValue = if (isPressed) 0f else 6f, 
        label = "shadow",
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    
    val currentModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = androidx.compose.material.ripple.rememberRipple(bounded = true),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .padding(bottom = 6.dp, end = 6.dp) // Space for shadow
            .then(currentModifier)
    ) {
        // Shadow (Static or Animated)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shadowOffset.dp, y = shadowOffset.dp)
                .background(shadowColor)
        )
        
        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = if(isPressed) 4.dp else 0.dp, y = if(isPressed) 4.dp else 0.dp)
                .border(4.dp, borderColor)
                .background(backgroundColor)
                .padding(4.dp)
        ) {
           content() 
           if (showHalftone) {
               HalftoneBackground(modifier = Modifier.matchParentSize())
           }
        }
    }
}

@Composable
fun ArtisticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    text: String? = null,
    icon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    activeColor: Color = PureBlack,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    activeContentColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val currentBg = if (isActive || isPressed) activeColor else backgroundColor
    val currentContent = if (isActive || isPressed) activeContentColor else contentColor
    
    val shadowSize by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 0.dp else 4.dp,
        label = "shadow",
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    val pressOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        label = "offset",
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = modifier
            .padding(bottom = 4.dp, end = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material.ripple.rememberRipple(bounded = true),
                onClick = onClick
            )
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowSize, y = shadowSize)
                .background(PureBlack)
        )
        
        // Content
        Box(
            modifier = Modifier
                .offset(x = pressOffset, y = pressOffset)
                .border(3.dp, PureBlack)
                .background(currentBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (text != null) {
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = currentContent
                )
            }
            if (icon != null) {
                Box(modifier = Modifier.padding(if(text!=null) 4.dp else 0.dp)) {
                    icon()
                }
            }
        }
    }
}

@Composable
fun MangaFX(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = PureBlack,
    rotation: Float = -12f
) {
    Text(
        text = text,
        style = MaterialTheme.typography.displayMedium.copy(
             fontStyle = FontStyle.Italic,
             fontWeight = FontWeight.Black
        ),
        color = color,
        modifier = modifier
            .rotate(rotation)
    )
}
