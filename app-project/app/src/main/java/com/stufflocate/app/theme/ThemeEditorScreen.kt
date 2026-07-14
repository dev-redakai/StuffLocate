package com.stufflocate.app.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditorScreen(
  initialTheme: AppTheme? = null,
  onBack: () -> Unit,
  onSaved: (AppTheme) -> Unit,
) {
  val isNew = initialTheme == null
  var themeName by remember { mutableStateOf(initialTheme?.name ?: "My Theme") }
  var isDark by remember { mutableStateOf(initialTheme?.isDark ?: false) }
  var primaryColor by remember { mutableStateOf(Color(initialTheme?.colors?.primary ?: 0xFF5B5FFF)) }
  var secondaryColor by remember { mutableStateOf(Color(initialTheme?.colors?.secondary ?: 0xFFFF5E7D)) }
  var tertiaryColor by remember { mutableStateOf(Color(initialTheme?.colors?.tertiary ?: 0xFF00D4AA)) }
  var backgroundColor by remember { mutableStateOf(Color(initialTheme?.colors?.background ?: 0xFFF8F9FA)) }
  var surfaceColor by remember { mutableStateOf(Color(initialTheme?.colors?.surface ?: 0xFFFFFFFF)) }
  var errorColor by remember { mutableStateOf(Color(initialTheme?.colors?.error ?: 0xFFEF4444)) }
  var glassStyle by remember { mutableStateOf(initialTheme?.glass?.style ?: GlassStyle.LIQUID) }
  var blurIntensity by remember { mutableFloatStateOf(initialTheme?.glass?.blurIntensity ?: 25f) }
  var glassAlpha by remember { mutableFloatStateOf(initialTheme?.glass?.alpha ?: 0.7f) }
  var cornerRadius by remember { mutableFloatStateOf(initialTheme?.glass?.cornerRadius ?: 24f) }
  var tintStrength by remember { mutableFloatStateOf(initialTheme?.glass?.tintStrength ?: 0.08f) }
  var borderAlpha by remember { mutableFloatStateOf(initialTheme?.glass?.borderAlpha ?: 0.3f) }
  var showColorPicker by remember { mutableStateOf(false) }
  var editingColorKey by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      LiquidGlassTopBar(
        title = if (isNew) "Create Theme" else "Edit Theme",
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          TextButton(onClick = {
            val theme = AppTheme(
              id = initialTheme?.id ?: "custom_${System.currentTimeMillis()}",
              name = themeName,
              isDark = isDark,
              colors = ThemeColors(
                primary = primaryColor.hashCode().toLong(),
                secondary = secondaryColor.hashCode().toLong(),
                tertiary = tertiaryColor.hashCode().toLong(),
                background = backgroundColor.hashCode().toLong(),
                surface = surfaceColor.hashCode().toLong(),
                surfaceVariant = backgroundColor.hashCode().toLong(),
                error = errorColor.hashCode().toLong(),
              ),
              glass = GlassConfig(
                style = glassStyle,
                blurIntensity = blurIntensity,
                alpha = glassAlpha,
                cornerRadius = cornerRadius,
                tintStrength = tintStrength,
                borderAlpha = borderAlpha,
              ),
            )
            onSaved(theme)
          }) {
            Text("Save", fontWeight = FontWeight.Bold, color = primaryColor)
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      // Theme Name
      LiquidGlassTextField(
        value = themeName,
        onValueChange = { themeName = it },
        label = "Theme Name",
        placeholder = "Enter theme name",
      )

      // Dark Mode Toggle
      LiquidGlassCard {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text("Dark Mode", fontWeight = FontWeight.SemiBold)
            Text(
              if (isDark) "Dark theme active" else "Light theme active",
              style = MaterialTheme.typography.bodySmall,
              color = Color(LocalAppTheme.current.colors.onSurfaceVariant),
            )
          }
          Switch(
            checked = isDark,
            onCheckedChange = {
              isDark = it
              if (it) {
                backgroundColor = Color(0xFF0F1114)
                surfaceColor = Color(0xFF1A1C20)
              } else {
                backgroundColor = Color(0xFFF8F9FA)
                surfaceColor = Color(0xFFFFFFFF)
              }
            },
            colors = SwitchDefaults.colors(
              checkedTrackColor = primaryColor,
            ),
          )
        }
      }

      // Color Palette
      LiquidGlassCard {
        Text("Color Palette", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))

        val colorItems = listOf(
          "Primary" to primaryColor,
          "Secondary" to secondaryColor,
          "Tertiary" to tertiaryColor,
          "Background" to backgroundColor,
          "Surface" to surfaceColor,
          "Error" to errorColor,
        )

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          items(colorItems) { (name, color) ->
            ColorSwatch(
              name = name,
              color = color,
              onClick = {
                editingColorKey = name
                showColorPicker = true
              },
            )
          }
        }
      }

      // Glass Style
      LiquidGlassCard {
        Text("Glass Style", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(GlassStyle.entries) { style ->
            GlassStyleChip(
              style = style,
              selected = glassStyle == style,
              onClick = { glassStyle = style },
            )
          }
        }
      }

      // Glass Parameters
      LiquidGlassCard {
        Text("Glass Parameters", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))

        SliderParameter("Blur Intensity", blurIntensity, 0f..60f) { blurIntensity = it }
        SliderParameter("Transparency", glassAlpha, 0.3f..1f) { glassAlpha = it }
        SliderParameter("Corner Radius", cornerRadius, 0f..48f) { cornerRadius = it }
        SliderParameter("Tint Strength", tintStrength, 0f..0.3f) { tintStrength = it }
        SliderParameter("Border Opacity", borderAlpha, 0f..0.8f) { borderAlpha = it }
      }

      // Preview
      LiquidGlassCard {
        Text("Preview", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          // Mini card preview
          LiquidGlassCard(
            modifier = Modifier.weight(1f),
            theme = AppTheme(
              colors = ThemeColors(
                primary = primaryColor.hashCode().toLong(),
                secondary = secondaryColor.hashCode().toLong(),
                background = backgroundColor.hashCode().toLong(),
                surface = surfaceColor.hashCode().toLong(),
              ),
              glass = GlassConfig(
                style = glassStyle,
                blurIntensity = blurIntensity,
                alpha = glassAlpha,
                cornerRadius = cornerRadius,
                tintStrength = tintStrength,
                borderAlpha = borderAlpha,
              ),
            ),
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(primaryColor.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center,
            ) {
              Text("Glass Card", color = primaryColor, fontWeight = FontWeight.Medium)
            }
          }

          // Mini button preview
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            LiquidGlassButton(
              text = "Primary",
              onClick = {},
              modifier = Modifier.fillMaxWidth(),
              theme = AppTheme(
                colors = ThemeColors(primary = primaryColor.hashCode().toLong()),
                glass = GlassConfig(style = glassStyle),
              ),
              style = ButtonStyle.FILLED,
            )
            LiquidGlassButton(
              text = "Glass",
              onClick = {},
              modifier = Modifier.fillMaxWidth(),
              theme = AppTheme(
                colors = ThemeColors(
                  primary = primaryColor.hashCode().toLong(),
                  surface = surfaceColor.hashCode().toLong(),
                ),
                glass = GlassConfig(style = glassStyle, alpha = glassAlpha),
              ),
              style = ButtonStyle.GLASS,
            )
          }
        }
      }
    }
  }

  // Color Picker Dialog
  if (showColorPicker) {
    ColorPickerDialog(
      color = when (editingColorKey) {
        "Primary" -> primaryColor
        "Secondary" -> secondaryColor
        "Tertiary" -> tertiaryColor
        "Background" -> backgroundColor
        "Surface" -> surfaceColor
        "Error" -> errorColor
        else -> primaryColor
      },
      onColorSelected = { color ->
        when (editingColorKey) {
          "Primary" -> primaryColor = color
          "Secondary" -> secondaryColor = color
          "Tertiary" -> tertiaryColor = color
          "Background" -> backgroundColor = color
          "Surface" -> surfaceColor = color
          "Error" -> errorColor = color
        }
        showColorPicker = false
      },
      onDismiss = { showColorPicker = false },
    )
  }
}

@Composable
private fun ColorSwatch(
  name: String,
  color: Color,
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable(onClick = onClick),
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(color)
        .border(2.dp, Color.White, CircleShape),
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(name, style = MaterialTheme.typography.labelSmall)
  }
}

@Composable
private fun GlassStyleChip(
  style: GlassStyle,
  selected: Boolean,
  onClick: () -> Unit,
) {
  val styleName = when (style) {
    GlassStyle.LIQUID -> "Liquid"
    GlassStyle.FROSTED -> "Frosted"
    GlassStyle.AURORA -> "Aurora"
    GlassStyle.NEON -> "Neon"
    GlassStyle.MINIMAL -> "Minimal"
  }

  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    color = if (selected) Color(0xFF5B5FFF).copy(alpha = 0.15f) else Color(0xFFF0F2F5),
    border = if (selected) {
      ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
        brush = androidx.compose.ui.graphics.Brush.linearGradient(
          colors = listOf(Color(0xFF5B5FFF).copy(alpha = 0.5f), Color(0xFF5B5FFF).copy(alpha = 0.3f))
        )
      )
    } else ButtonDefaults.outlinedButtonBorder(enabled = true),
  ) {
    Text(
      styleName,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      style = MaterialTheme.typography.labelMedium,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
      color = if (selected) Color(0xFF5B5FFF) else Color(0xFF1A1C1E),
    )
  }
}

@Composable
private fun SliderParameter(
  label: String,
  value: Float,
  range: ClosedFloatingPointRange<Float>,
  onValueChange: (Float) -> Unit,
) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(label, style = MaterialTheme.typography.bodySmall)
      Text(String.format("%.1f", value), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = range,
      colors = SliderDefaults.colors(
        thumbColor = Color(0xFF5B5FFF),
        activeTrackColor = Color(0xFF5B5FFF),
      ),
    )
  }
}

@Composable
fun ColorPickerDialog(
  color: Color,
  onColorSelected: (Color) -> Unit,
  onDismiss: () -> Unit,
) {
  val presetColors = listOf(
    Color(0xFF5B5FFF), Color(0xFFFF5E7D), Color(0xFF00D4AA),
    Color(0xFFEF4444), Color(0xFF22C55E), Color(0xFFF59E0B),
    Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFFEC4899),
    Color(0xFF14B8A6), Color(0xFFF97316), Color(0xFF06B6D4),
    Color(0xFF10B981), Color(0xFF6366F1), Color(0xFFD946EF),
    Color(0xFF0EA5E9), Color(0xFF84CC16), Color(0xFFE11D48),
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFFFFFFFF),
    shape = RoundedCornerShape(28.dp),
    title = { Text("Pick Color", fontWeight = FontWeight.Bold) },
    text = {
      Column {
        // Current color preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color),
          contentAlignment = Alignment.Center,
        ) {
          Text("Selected", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset colors grid
        Text("Presets", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          presetColors.chunked(6).forEach { row ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              row.forEach { preset ->
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(preset)
                    .clickable { onColorSelected(preset) }
                    .border(2.dp, Color.White, CircleShape),
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hex input
        var hexInput by remember { mutableStateOf(String.format("#%06X", 0xFFFFFF and color.hashCode())) }
        LiquidGlassTextField(
          value = hexInput,
          onValueChange = { hex ->
            hexInput = hex
            try {
              if (hex.startsWith("#") && hex.length == 7) {
                val colorInt = android.graphics.Color.parseColor(hex)
                onColorSelected(Color(colorInt))
              }
            } catch (_: Exception) {}
          },
          label = "Hex Color",
          placeholder = "#5B5FFF",
        )
      }
    },
    confirmButton = {
      LiquidGlassButton(
        text = "Done",
        onClick = onDismiss,
        style = ButtonStyle.FILLED,
      )
    },
  )
}
