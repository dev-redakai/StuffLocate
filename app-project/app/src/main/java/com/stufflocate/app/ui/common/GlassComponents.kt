package com.stufflocate.app.ui.common

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── iOS-Inspired Colors ─────────────────────────────────────────────

object IOSColors {
  var Primary = Color(0xFF5B5FFF); private set
  var PrimaryLight = Color(0xFF8386FF); private set
  var Secondary = Color(0xFFFF5E7D); private set
  var Cyan = Color(0xFF00D4AA); private set
  var Blue = Color(0xFF007AFF); private set
  var Green = Color(0xFF34C759); private set
  var Orange = Color(0xFFFF9500); private set
  var Red = Color(0xFFFF3B30); private set
  var Indigo = Color(0xFF5856D6); private set
  var Purple = Color(0xFFAF52DE); private set
  var Yellow = Color(0xFFFFCC00); private set
  var Teal = Color(0xFF5AC8FA); private set

  fun updateFromTheme(theme: com.stufflocate.app.theme.AppTheme) {
    Primary = Color(theme.colors.primary)
    PrimaryLight = Color(theme.colors.primary).copy(alpha = 0.7f)
    Secondary = Color(theme.colors.secondary)
    Cyan = Color(theme.colors.tertiary)
    Green = Color(theme.colors.tertiary)
    Orange = Color(theme.colors.warning)
    Red = Color(theme.colors.error)
    Indigo = Color(theme.colors.primary).copy(alpha = 0.8f)
    Purple = Color(theme.colors.secondary).copy(alpha = 0.8f)
    Yellow = Color(theme.colors.warning)
    Teal = Color(theme.colors.tertiary).copy(alpha = 0.8f)
    Blue = Color(theme.colors.primary)
  }
}

// ─── Card Styles ─────────────────────────────────────────────────────

@Composable
fun ModernCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  shape: RoundedCornerShape = RoundedCornerShape(16.dp),
  elevation: Dp = 2.dp,
  content: @Composable ColumnScope.() -> Unit,
) {
  val colors = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surface,
  )

  if (onClick != null) {
    Card(
      onClick = onClick,
      modifier = modifier,
      shape = shape,
      colors = colors,
      elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
      Column(modifier = Modifier.padding(16.dp), content = content)
    }
  } else {
    Card(
      modifier = modifier,
      shape = shape,
      colors = colors,
      elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
      Column(modifier = Modifier.padding(16.dp), content = content)
    }
  }
}

@Composable
fun ModernCardRow(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: RoundedCornerShape = RoundedCornerShape(16.dp),
  elevation: Dp = 2.dp,
  content: @Composable RowScope.() -> Unit,
) {
  Card(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      content = content,
    )
  }
}

// ─── iOS-style Rounded Icon Box ──────────────────────────────────────

@Composable
fun RoundIconBox(
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  color: Color = IOSColors.Primary,
  alpha: Float = 0.12f,
) {
  Surface(
    modifier = modifier.size(size),
    shape = RoundedCornerShape(14.dp),
    color = color.copy(alpha = alpha),
  ) {
    Box(contentAlignment = Alignment.Center) {
      icon()
    }
  }
}

// ─── Gradient Header ─────────────────────────────────────────────────

@Composable
fun GradientHeader(
  modifier: Modifier = Modifier,
  colors: List<Color> = listOf(IOSColors.Primary, IOSColors.Indigo),
  content: @Composable ColumnScope.() -> Unit,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
    color = Color.Transparent,
  ) {
    Box(modifier = Modifier.background(
      brush = Brush.horizontalGradient(colors = colors),
      shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
    )) {
      Column(
        modifier = Modifier.padding(24.dp),
        content = content,
      )
    }
  }
}

// ─── Badge / Tag ─────────────────────────────────────────────────────

@Composable
fun ModernBadge(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = IOSColors.Primary,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = color.copy(alpha = 0.12f),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelMedium,
      color = color,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
    )
  }
}

// ─── Stylish TextField ───────────────────────────────────────────────

@Composable
fun StyledTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  singleLine: Boolean = true,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    singleLine = singleLine,
    shape = RoundedCornerShape(16.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = IOSColors.Primary.copy(alpha = 0.6f),
      unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
      focusedContainerColor = MaterialTheme.colorScheme.surface,
      unfocusedContainerColor = MaterialTheme.colorScheme.surface,
      cursorColor = IOSColors.Primary,
    ),
  )
}
