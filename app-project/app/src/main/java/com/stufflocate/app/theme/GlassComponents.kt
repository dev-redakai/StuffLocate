package com.stufflocate.app.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun LiquidGlassCard(
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  onClick: (() -> Unit)? = null,
  cornerRadius: Dp = theme.glass.cornerRadius.dp,
  blurIntensity: Float = theme.glass.blurIntensity,
  alpha: Float = theme.glass.alpha,
  borderAlpha: Float = theme.glass.borderAlpha,
  content: @Composable ColumnScope.() -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))

  val primaryColor = Color(theme.colors.primary)
  val surfaceColor = Color(theme.colors.surface)

  val shape = RoundedCornerShape(cornerRadius)
  val glassBrush = when (theme.glass.style) {
    GlassStyle.LIQUID -> Brush.verticalGradient(
      colors = listOf(
        surfaceColor.copy(alpha = alpha + 0.1f),
        surfaceColor.copy(alpha = alpha),
        primaryColor.copy(alpha = theme.glass.tintStrength),
      )
    )
    GlassStyle.FROSTED -> Brush.linearGradient(
      colors = listOf(
        Color.White.copy(alpha = alpha * 0.8f),
        surfaceColor.copy(alpha = alpha * 0.6f),
        Color.White.copy(alpha = alpha * 0.7f),
      )
    )
    GlassStyle.AURORA -> Brush.sweepGradient(
      colors = listOf(
        primaryColor.copy(alpha = 0.15f),
        Color(theme.colors.secondary).copy(alpha = 0.1f),
        Color(theme.colors.tertiary).copy(alpha = 0.12f),
        primaryColor.copy(alpha = 0.15f),
      )
    )
    GlassStyle.NEON -> Brush.verticalGradient(
      colors = listOf(
        primaryColor.copy(alpha = 0.12f),
        surfaceColor.copy(alpha = alpha * 0.5f),
        primaryColor.copy(alpha = 0.08f),
      )
    )
    GlassStyle.MINIMAL -> Brush.verticalGradient(
      colors = listOf(
        surfaceColor.copy(alpha = alpha + 0.05f),
        surfaceColor.copy(alpha = alpha),
      )
    )
  }

  val borderBrush = Brush.linearGradient(
    colors = listOf(
      Color.White.copy(alpha = borderAlpha),
      primaryColor.copy(alpha = borderAlpha * 0.5f),
      Color.White.copy(alpha = borderAlpha * 0.3f),
    )
  )

  val clickableModifier = if (onClick != null) {
    Modifier.clickable(
      interactionSource = interactionSource,
      indication = null,
      onClick = onClick,
    )
  } else Modifier

  Column(
    modifier = modifier
      .scale(scale)
      .clip(shape)
      .background(glassBrush, shape)
      .then(
        if (theme.glass.style == GlassStyle.NEON) {
          Modifier.drawBehind {
            drawRoundRect(
              color = primaryColor.copy(alpha = 0.15f),
              cornerRadius = CornerRadius(cornerRadius.toPx() + 4f, cornerRadius.toPx() + 4f),
              size = size.copy(width = size.width + 8f, height = size.height + 8f),
              topLeft = Offset(-4f, -4f),
            )
          }
        } else Modifier
      )
      .border(1.dp, borderBrush, shape)
      .then(clickableModifier)
      .padding(16.dp),
    content = content,
  )
}

@Composable
fun LiquidGlassButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  enabled: Boolean = true,
  icon: @Composable (() -> Unit)? = null,
  style: ButtonStyle = ButtonStyle.FILLED,
) {
  val primaryColor = Color(theme.colors.primary)
  val onPrimaryColor = Color(theme.colors.onPrimary)
  val surfaceColor = Color(theme.colors.surface)

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))

  val shape = RoundedCornerShape(16.dp)

  val brush = when (style) {
    ButtonStyle.FILLED -> Brush.horizontalGradient(
      colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
    )
    ButtonStyle.GLASS -> Brush.verticalGradient(
      colors = listOf(
        surfaceColor.copy(alpha = theme.glass.alpha),
        surfaceColor.copy(alpha = theme.glass.alpha * 0.8f),
      )
    )
    ButtonStyle.OUTLINE -> Brush.linearGradient(
      colors = listOf(Color.Transparent, Color.Transparent)
    )
  }

  val textColor = when (style) {
    ButtonStyle.FILLED -> onPrimaryColor
    ButtonStyle.GLASS -> Color(theme.colors.onSurface)
    ButtonStyle.OUTLINE -> primaryColor
  }

  val borderModifier = when (style) {
    ButtonStyle.GLASS -> Modifier.border(1.dp, primaryColor.copy(alpha = 0.3f), shape)
    ButtonStyle.OUTLINE -> Modifier.border(1.5.dp, primaryColor, shape)
    else -> Modifier
  }

  Surface(
    onClick = onClick,
    modifier = modifier
      .scale(scale)
      .clip(shape)
      .then(borderModifier),
    shape = shape,
    enabled = enabled,
    color = Color.Transparent,
    interactionSource = interactionSource,
  ) {
    Row(
      modifier = Modifier
        .background(brush, shape)
        .padding(horizontal = 24.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (icon != null) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
      )
    }
  }
}

enum class ButtonStyle {
  FILLED,
  GLASS,
  OUTLINE,
}

@Composable
fun LiquidGlassTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  label: String = "",
  placeholder: String = "",
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  singleLine: Boolean = true,
) {
  val primaryColor = Color(theme.colors.primary)
  val surfaceColor = Color(theme.colors.surface)
  val onSurfaceColor = Color(theme.colors.onSurfaceVariant)

  val shape = RoundedCornerShape(theme.glass.cornerRadius.dp / 2)

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.fillMaxWidth(),
    label = { Text(label) },
    placeholder = { Text(placeholder, color = onSurfaceColor.copy(alpha = 0.5f)) },
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    singleLine = singleLine,
    shape = shape,
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = primaryColor,
      unfocusedBorderColor = Color(theme.colors.outline),
      focusedContainerColor = surfaceColor.copy(alpha = theme.glass.alpha * 0.5f),
      unfocusedContainerColor = surfaceColor.copy(alpha = theme.glass.alpha * 0.3f),
      focusedTextColor = Color(theme.colors.onSurface),
      unfocusedTextColor = Color(theme.colors.onSurface),
      cursorColor = primaryColor,
      focusedLabelColor = primaryColor,
      unfocusedLabelColor = onSurfaceColor,
    ),
  )
}

@Composable
fun LiquidGlassBottomBar(
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  content: @Composable RowScope.() -> Unit,
) {
  val surfaceColor = Color(theme.colors.surface)
  val primaryColor = Color(theme.colors.primary)

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    color = Color.Transparent,
    shadowElevation = 12.dp,
    tonalElevation = 0.dp,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              surfaceColor.copy(alpha = theme.glass.alpha + 0.1f),
              surfaceColor.copy(alpha = theme.glass.alpha),
            )
          ),
          RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        )
        .border(
          1.dp,
          Brush.horizontalGradient(
            colors = listOf(
              Color.White.copy(alpha = theme.glass.borderAlpha),
              primaryColor.copy(alpha = theme.glass.borderAlpha * 0.3f),
              Color.White.copy(alpha = theme.glass.borderAlpha),
            )
          ),
          RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        )
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      content = content,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassTopBar(
  title: String,
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  navigationIcon: @Composable (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  val surfaceColor = Color(theme.colors.surface)
  val primaryColor = Color(theme.colors.primary)

  TopAppBar(
    title = {
      Text(
        title,
        fontWeight = FontWeight.Bold,
        color = Color(theme.colors.onSurface),
      )
    },
    modifier = modifier,
    navigationIcon = { navigationIcon?.invoke() },
    actions = actions,
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = Color.Transparent,
      titleContentColor = Color(theme.colors.onSurface),
      navigationIconContentColor = Color(theme.colors.onSurface),
      actionIconContentColor = Color(theme.colors.onSurfaceVariant),
    ),
  )
}

@Composable
fun LiquidGlassChip(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  icon: @Composable (() -> Unit)? = null,
) {
  val primaryColor = Color(theme.colors.primary)
  val surfaceColor = Color(theme.colors.surface)
  val shape = RoundedCornerShape(12.dp)

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))

  Surface(
    onClick = onClick,
    modifier = modifier.scale(scale),
    shape = shape,
    color = if (selected) primaryColor.copy(alpha = 0.15f) else surfaceColor.copy(alpha = theme.glass.alpha),
    border = if (selected) {
      ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
        brush = Brush.linearGradient(
          colors = listOf(primaryColor.copy(alpha = 0.5f), primaryColor.copy(alpha = 0.3f))
        )
      )
    } else {
      ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
        brush = Brush.linearGradient(
          colors = listOf(
            Color(theme.colors.outline).copy(alpha = 0.5f),
            Color(theme.colors.outline).copy(alpha = 0.3f),
          )
        )
      )
    },
    interactionSource = interactionSource,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      if (icon != null) icon()
      Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = if (selected) primaryColor else Color(theme.colors.onSurface),
      )
    }
  }
}

@Composable
fun LiquidGlassSurface(
  modifier: Modifier = Modifier,
  theme: AppTheme = LocalAppTheme.current,
  shape: Shape = RoundedCornerShape(theme.glass.cornerRadius.dp),
  content: @Composable () -> Unit,
) {
  val surfaceColor = Color(theme.colors.surface)
  val primaryColor = Color(theme.colors.primary)

  Surface(
    modifier = modifier,
    shape = shape,
    color = Color.Transparent,
    shadowElevation = 8.dp,
    tonalElevation = 0.dp,
  ) {
    Box(
      modifier = Modifier
        .background(
          Brush.verticalGradient(
            colors = listOf(
              surfaceColor.copy(alpha = theme.glass.alpha + 0.1f),
              surfaceColor.copy(alpha = theme.glass.alpha),
              primaryColor.copy(alpha = theme.glass.tintStrength),
            )
          ),
          shape,
        )
        .border(
          1.dp,
          Brush.linearGradient(
            colors = listOf(
              Color.White.copy(alpha = theme.glass.borderAlpha),
              primaryColor.copy(alpha = theme.glass.borderAlpha * 0.3f),
            )
          ),
          shape,
        ),
    ) {
      content()
    }
  }
}

@Composable
fun AnimatedGlowEffect(
  modifier: Modifier = Modifier,
  color: Color,
  enabled: Boolean = true,
  content: @Composable () -> Unit,
) {
  if (!enabled) {
    content()
    return
  }

  val infiniteTransition = rememberInfiniteTransition()
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.7f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    )
  )

  Box(
    modifier = modifier.drawBehind {
      drawCircle(
        color = color.copy(alpha = glowAlpha * 0.3f),
        radius = size.maxDimension * 0.55f,
      )
      drawCircle(
        color = color.copy(alpha = glowAlpha * 0.15f),
        radius = size.maxDimension * 0.6f,
      )
    },
  ) {
    content()
  }
}

val LocalAppTheme = staticCompositionLocalOf { ThemePresets.LIQUID_GLASS_LIGHT }
