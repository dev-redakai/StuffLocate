package com.stufflocate.app.ui.about

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stufflocate.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
  val theme = LocalAppTheme.current
  val primaryColor = Color(theme.colors.primary)
  val secondaryColor = Color(theme.colors.secondary)
  val tertiaryColor = Color(theme.colors.tertiary)
  val backgroundColor = Color(theme.colors.background)
  val onSurfaceColor = Color(theme.colors.onSurface)
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)
  val surfaceColor = Color(theme.colors.surface)
  val errorColor = Color(theme.colors.error)

  val infiniteTransition = rememberInfiniteTransition()
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
  )

  Scaffold(
    containerColor = backgroundColor,
    topBar = {
      LiquidGlassTopBar(
        title = "About",
        theme = theme,
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceColor)
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
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      Spacer(Modifier.height(8.dp))

      // Hero Section with animated glow
      Box(contentAlignment = Alignment.Center) {
        AnimatedGlowEffect(color = primaryColor, enabled = true) {
          Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(28.dp),
            color = primaryColor,
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color.White,
              )
            }
          }
        }
      }

      Text(
        "Stuff Locate",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = onSurfaceColor,
      )
      Text(
        "Smart Home Storage Organizer",
        style = MaterialTheme.typography.titleMedium,
        color = primaryColor,
        fontWeight = FontWeight.Medium,
      )
      Text(
        "v1.0.0",
        style = MaterialTheme.typography.bodySmall,
        color = onSurfaceVariantColor,
      )

      // Description Card
      LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
        Text(
          "Keep track of everything in your home.",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          color = onSurfaceColor,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
          "From kitchen appliances to garage tools, know exactly where everything is stored. Create 3D floor plans, tag items by room and furniture, and never lose track of your belongings again.",
          style = MaterialTheme.typography.bodyMedium,
          color = onSurfaceVariantColor,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )
      }

      // Stats Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        GlassStatItem("10", "Themes", primaryColor, theme, Modifier.weight(1f))
        GlassStatItem("19", "Room Types", secondaryColor, theme, Modifier.weight(1f))
        GlassStatItem("12", "Categories", tertiaryColor, theme, Modifier.weight(1f))
      }

      // Features
      Text(
        "Features",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = primaryColor,
        modifier = Modifier.fillMaxWidth(),
      )

      LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
        GlassFeatureItem(Icons.Default.Home, "Multiple Homes", "Manage unlimited homes, floors & rooms", primaryColor, theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.GridOn, "Floor Plan Editor", "Draw 2D room layouts with wall tools", secondaryColor, theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.ViewInAr, "3D Preview", "Isometric view of your floor plans", tertiaryColor, theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.Inventory2, "Furniture System", "10 furniture types with named slots", Color(theme.colors.warning), theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.CameraAlt, "Photo Capture", "Item photos + location photos", Color(theme.colors.success), theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.Search, "Smart Search", "Filter by category, status, room", primaryColor, theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.Palette, "Liquid Glass Themes", "10 presets + custom theme creator", secondaryColor, theme)
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassFeatureItem(Icons.Default.Share, "Export & Import", "JSON backup of your entire inventory", tertiaryColor, theme)
      }

      // Tech Stack
      Text(
        "Tech Stack",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = primaryColor,
        modifier = Modifier.fillMaxWidth(),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        GlassTechChip("Kotlin", primaryColor, theme, Modifier.weight(1f))
        GlassTechChip("Compose", secondaryColor, theme, Modifier.weight(1f))
        GlassTechChip("Room DB", tertiaryColor, theme, Modifier.weight(1f))
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        GlassTechChip("Navigation3", Color(theme.colors.warning), theme, Modifier.weight(1f))
        GlassTechChip("CameraX", Color(theme.colors.success), theme, Modifier.weight(1f))
        GlassTechChip("Coil", Color(theme.colors.error), theme, Modifier.weight(1f))
      }

      // Credits / Open Source
      Text(
        "Credits & Open Source",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = primaryColor,
        modifier = Modifier.fillMaxWidth(),
      )

      LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
        GlassCreditItem(
          icon = Icons.Default.Code,
          title = "Open Source Libraries",
          subtitle = "Jetpack Compose, Material3, Room, Coil, CameraX, Navigation3",
          color = primaryColor,
          theme = theme,
        )
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassCreditItem(
          icon = Icons.Default.DesignServices,
          title = "Design System",
          subtitle = "Custom Liquid Glass morphism with 10 preset themes",
          color = secondaryColor,
          theme = theme,
        )
        HorizontalDivider(color = Color(theme.colors.outline).copy(alpha = 0.2f))
        GlassCreditItem(
          icon = Icons.Default.PhoneAndroid,
          title = "Platform",
          subtitle = "Android 8.0+ (API 26+), Kotlin 2.0, Gradle",
          color = tertiaryColor,
          theme = theme,
        )
      }

      // Footer
      LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
          Text(
            "Made with love for organized living",
            style = MaterialTheme.typography.bodyMedium,
            color = onSurfaceVariantColor,
            textAlign = TextAlign.Center,
          )
          Spacer(Modifier.height(6.dp))
          Text(
            "2026 Stuff Locate",
            style = MaterialTheme.typography.labelSmall,
            color = onSurfaceVariantColor.copy(alpha = 0.5f),
          )
        }
      }

      Spacer(Modifier.height(16.dp))
    }
  }
}

@Composable
private fun GlassStatItem(
  value: String,
  label: String,
  color: Color,
  theme: AppTheme,
  modifier: Modifier,
) {
  LiquidGlassCard(modifier = modifier, theme = theme) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = color)
      Spacer(Modifier.height(4.dp))
      Text(label, style = MaterialTheme.typography.labelSmall, color = Color(theme.colors.onSurfaceVariant))
    }
  }
}

@Composable
private fun GlassFeatureItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  color: Color,
  theme: AppTheme,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      modifier = Modifier.size(40.dp),
      shape = RoundedCornerShape(12.dp),
      color = color.copy(alpha = 0.12f),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      }
    }
    Spacer(Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Color(theme.colors.onSurface))
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(theme.colors.onSurfaceVariant))
    }
  }
}

@Composable
private fun GlassTechChip(
  text: String,
  color: Color,
  theme: AppTheme,
  modifier: Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.1f),
    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
      brush = Brush.linearGradient(colors = listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.15f)))
    ),
  ) {
    Text(
      text,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.SemiBold,
      color = color,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
    )
  }
}

@Composable
private fun GlassCreditItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  color: Color,
  theme: AppTheme,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      modifier = Modifier.size(38.dp),
      shape = RoundedCornerShape(12.dp),
      color = color.copy(alpha = 0.12f),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
      }
    }
    Spacer(Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color(theme.colors.onSurface))
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(theme.colors.onSurfaceVariant))
    }
  }
}
