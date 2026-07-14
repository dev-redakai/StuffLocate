package com.stufflocate.app.theme

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AppTheme(
  val id: String = "",
  val name: String = "",
  val isDark: Boolean = false,
  val colors: ThemeColors = ThemeColors(),
  val glass: GlassConfig = GlassConfig(),
  val typography: ThemeTypography = ThemeTypography(),
  val animation: AnimationConfig = AnimationConfig(),
)

@Serializable
data class ThemeColors(
  val primary: Long = 0xFF5B5FFF,
  val secondary: Long = 0xFFFF5E7D,
  val tertiary: Long = 0xFF00D4AA,
  val background: Long = 0xFFF8F9FA,
  val surface: Long = 0xFFFFFFFF,
  val surfaceVariant: Long = 0xFFF0F2F5,
  val onPrimary: Long = 0xFFFFFFFF,
  val onSecondary: Long = 0xFFFFFFFF,
  val onBackground: Long = 0xFF1A1C1E,
  val onSurface: Long = 0xFF1A1C1E,
  val onSurfaceVariant: Long = 0xFF6B7280,
  val error: Long = 0xFFEF4444,
  val success: Long = 0xFF22C55E,
  val warning: Long = 0xFFF59E0B,
  val outline: Long = 0xFFE5E7EB,
  val outlineVariant: Long = 0xFFF3F4F6,
)

@Serializable
data class GlassConfig(
  val enabled: Boolean = true,
  val blurIntensity: Float = 20f,
  val alpha: Float = 0.7f,
  val cornerRadius: Float = 24f,
  val borderAlpha: Float = 0.3f,
  val shadowAlpha: Float = 0.1f,
  val tintStrength: Float = 0.08f,
  val style: GlassStyle = GlassStyle.LIQUID,
)

@Serializable
enum class GlassStyle {
  LIQUID,
  FROSTED,
  AURORA,
  NEON,
  MINIMAL,
}

@Serializable
data class ThemeTypography(
  val fontFamily: String = "default",
  val titleSize: Float = 28f,
  val bodySize: Float = 16f,
  val labelSize: Float = 12f,
  val letterSpacing: Float = 0f,
  val lineHeight: Float = 1.4f,
)

@Serializable
data class AnimationConfig(
  val enabled: Boolean = true,
  val speed: Float = 1f,
  val pageTransitions: Boolean = true,
  val cardAnimations: Boolean = true,
  val microInteractions: Boolean = true,
  val springStiffness: Float = 300f,
  val springDamping: Float = 0.8f,
)

object ThemePresets {
  val ALL get() = listOf(
    LIQUID_GLASS_LIGHT,
    LIQUID_GLASS_DARK,
    AURORA_BOREALIS,
    OCEAN_BREEZE,
    SUNSET_GLOW,
    FOREST_MIST,
    MIDNIGHT_NEON,
    ARCTIC_FROST,
    ROSE_GOLD,
    CYBER_PUNK,
  )

  val LIQUID_GLASS_LIGHT = AppTheme(
    id = "liquid_glass_light",
    name = "Liquid Glass",
    isDark = false,
    colors = ThemeColors(
      primary = 0xFF5B5FFF,
      secondary = 0xFFFF5E7D,
      tertiary = 0xFF00D4AA,
      background = 0xFFF8F9FA,
      surface = 0xFFFFFFFF,
      surfaceVariant = 0xFFF0F2F5,
    ),
    glass = GlassConfig(
      style = GlassStyle.LIQUID,
      blurIntensity = 25f,
      alpha = 0.72f,
      cornerRadius = 28f,
    ),
  )

  val LIQUID_GLASS_DARK = AppTheme(
    id = "liquid_glass_dark",
    name = "Liquid Glass Dark",
    isDark = true,
    colors = ThemeColors(
      primary = 0xFF7B7FFF,
      secondary = 0xFFFF7E9D,
      tertiary = 0xFF33E8C1,
      background = 0xFF0F1114,
      surface = 0xFF1A1C20,
      surfaceVariant = 0xFF252830,
      onBackground = 0xFFE8EAED,
      onSurface = 0xFFE8EAED,
      onSurfaceVariant = 0xFF9AA0A6,
      outline = 0xFF3C4043,
      outlineVariant = 0xFF2C2F33,
    ),
    glass = GlassConfig(
      style = GlassStyle.LIQUID,
      blurIntensity = 30f,
      alpha = 0.65f,
      cornerRadius = 28f,
      borderAlpha = 0.2f,
    ),
  )

  val AURORA_BOREALIS = AppTheme(
    id = "aurora_borealis",
    name = "Aurora Borealis",
    isDark = true,
    colors = ThemeColors(
      primary = 0xFF00E5A0,
      secondary = 0xFF7B61FF,
      tertiary = 0xFF00B4D8,
      background = 0xFF0A1628,
      surface = 0xFF131D2E,
      surfaceVariant = 0xFF1A2538,
      onBackground = 0xFFE0F2E9,
      onSurface = 0xFFE0F2E9,
    ),
    glass = GlassConfig(
      style = GlassStyle.AURORA,
      blurIntensity = 35f,
      alpha = 0.55f,
      tintStrength = 0.12f,
    ),
  )

  val OCEAN_BREEZE = AppTheme(
    id = "ocean_breeze",
    name = "Ocean Breeze",
    isDark = false,
    colors = ThemeColors(
      primary = 0xFF0077B6,
      secondary = 0xFF00B4D8,
      tertiary = 0xFF90E0EF,
      background = 0xFFF0F8FF,
      surface = 0xFFFFFFFF,
      surfaceVariant = 0xFFE8F4FD,
    ),
    glass = GlassConfig(
      style = GlassStyle.FROSTED,
      blurIntensity = 22f,
      alpha = 0.75f,
    ),
  )

  val SUNSET_GLOW = AppTheme(
    id = "sunset_glow",
    name = "Sunset Glow",
    isDark = false,
    colors = ThemeColors(
      primary = 0xFFFF6B35,
      secondary = 0xFFFFBE0B,
      tertiary = 0xFFFB5607,
      background = 0xFFFFF8F0,
      surface = 0xFFFFFFFF,
      surfaceVariant = 0xFFFFF0E0,
    ),
    glass = GlassConfig(
      style = GlassStyle.LIQUID,
      blurIntensity = 18f,
      tintStrength = 0.1f,
    ),
  )

  val FOREST_MIST = AppTheme(
    id = "forest_mist",
    name = "Forest Mist",
    isDark = false,
    colors = ThemeColors(
      primary = 0xFF2D6A4F,
      secondary = 0xFF52B788,
      tertiary = 0xFF95D5B2,
      background = 0xFFF0F7F0,
      surface = 0xFFFFFFFF,
      surfaceVariant = 0xFFE8F5E8,
    ),
    glass = GlassConfig(
      style = GlassStyle.FROSTED,
      blurIntensity = 20f,
      tintStrength = 0.06f,
    ),
  )

  val MIDNIGHT_NEON = AppTheme(
    id = "midnight_neon",
    name = "Midnight Neon",
    isDark = true,
    colors = ThemeColors(
      primary = 0xFFFF006E,
      secondary = 0xFF3A86FF,
      tertiary = 0xFFFB5607,
      background = 0xFF0A0A0F,
      surface = 0xFF141420,
      surfaceVariant = 0xFF1E1E30,
      onBackground = 0xFFF0F0FF,
      onSurface = 0xFFF0F0FF,
    ),
    glass = GlassConfig(
      style = GlassStyle.NEON,
      blurIntensity = 28f,
      alpha = 0.6f,
      borderAlpha = 0.4f,
      tintStrength = 0.15f,
    ),
  )

  val ARCTIC_FROST = AppTheme(
    id = "arctic_frost",
    name = "Arctic Frost",
    isDark = false,
    colors = ThemeColors(
      primary = 0xFF48CAE4,
      secondary = 0xFF90E0EF,
      tertiary = 0xFFCAF0F8,
      background = 0xFFF5FDFF,
      surface = 0xFFFFFFFF,
      surfaceVariant = 0xFFEAF6FB,
    ),
    glass = GlassConfig(
      style = GlassStyle.FROSTED,
      blurIntensity = 30f,
      alpha = 0.8f,
      tintStrength = 0.04f,
    ),
  )

  val ROSE_GOLD = AppTheme(
    id = "rose_gold",
    name = "Rose Gold",
    isDark = false,
    colors = ThemeColors(
      primary = 0xFFB76E79,
      secondary = 0xFFE8B4B8,
      tertiary = 0xFFF4C2C2,
      background = 0xFFFFF5F5,
      surface = 0xFFFFFFFF,
      surfaceVariant = 0xFFFFF0F0,
    ),
    glass = GlassConfig(
      style = GlassStyle.LIQUID,
      blurIntensity = 22f,
      alpha = 0.75f,
      tintStrength = 0.08f,
    ),
  )

  val CYBER_PUNK = AppTheme(
    id = "cyber_punk",
    name = "Cyber Punk",
    isDark = true,
    colors = ThemeColors(
      primary = 0xFF00F0FF,
      secondary = 0xFFFF00AA,
      tertiary = 0xFFFFFF00,
      background = 0xFF0D0221,
      surface = 0xFF150538,
      surfaceVariant = 0xFF1F0A4A,
      onBackground = 0xFFE0E0FF,
      onSurface = 0xFFE0E0FF,
    ),
    glass = GlassConfig(
      style = GlassStyle.NEON,
      blurIntensity = 25f,
      alpha = 0.5f,
      borderAlpha = 0.5f,
      tintStrength = 0.2f,
    ),
  )
}

class AppThemeManager(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
  private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

  private val _currentThemeFlow = MutableStateFlow(loadTheme() ?: ThemePresets.LIQUID_GLASS_LIGHT)
  val currentThemeFlow: StateFlow<AppTheme> = _currentThemeFlow.asStateFlow()

  var currentTheme: AppTheme
    get() = _currentThemeFlow.value
    set(value) {
      _currentThemeFlow.value = value
      saveTheme(value)
    }

  var customThemes: List<AppTheme>
    get() = loadCustomThemes()
    set(value) {
      saveCustomThemes(value)
    }

  private fun loadTheme(): AppTheme? {
    val jsonStr = prefs.getString("current_theme", null) ?: return null
    return try {
      json.decodeFromString<AppTheme>(jsonStr)
    } catch (e: Exception) {
      null
    }
  }

  private fun saveTheme(theme: AppTheme) {
    prefs.edit().putString("current_theme", json.encodeToString(theme)).apply()
  }

  private fun loadCustomThemes(): List<AppTheme> {
    val jsonStr = prefs.getString("custom_themes", "[]") ?: return emptyList()
    return try {
      json.decodeFromString(jsonStr)
    } catch (e: Exception) {
      emptyList()
    }
  }

  private fun saveCustomThemes(themes: List<AppTheme>) {
    prefs.edit().putString("custom_themes", json.encodeToString(themes)).apply()
  }

  fun saveCustomTheme(theme: AppTheme) {
    val themes = customThemes.toMutableList()
    val existingIndex = themes.indexOfFirst { it.id == theme.id }
    if (existingIndex >= 0) {
      themes[existingIndex] = theme
    } else {
      themes.add(theme)
    }
    customThemes = themes
    currentTheme = theme
  }

  fun deleteCustomTheme(themeId: String) {
    customThemes = customThemes.filter { it.id != themeId }
    if (currentTheme.id == themeId) {
      currentTheme = ThemePresets.LIQUID_GLASS_LIGHT
    }
  }

  fun setPresetTheme(theme: AppTheme) {
    currentTheme = theme.copy(id = theme.id)
  }

  fun exportTheme(theme: AppTheme): String {
    return json.encodeToString(theme)
  }

  fun importTheme(jsonStr: String): AppTheme? {
    return try {
      val theme = json.decodeFromString<AppTheme>(jsonStr)
      val importedTheme = theme.copy(id = "imported_${System.currentTimeMillis()}")
      saveCustomTheme(importedTheme)
      importedTheme
    } catch (e: Exception) {
      null
    }
  }

  fun getAllThemes(): List<AppTheme> {
    return ThemePresets.ALL + customThemes
  }

  fun getCurrentThemeName(): String {
    return currentTheme.name
  }

  fun isDarkMode(): Boolean {
    return currentTheme.isDark
  }
}
