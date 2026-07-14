package com.stufflocate.app.theme

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.ui.common.IOSColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
  private val _isDarkTheme = MutableStateFlow(false)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private var prefs: android.content.SharedPreferences? = null

  fun init(context: Context) {
    prefs = context.getSharedPreferences("stuff_locate_prefs", Context.MODE_PRIVATE)
    _isDarkTheme.value = prefs?.getBoolean("dark_mode", false) ?: false
  }

  fun setDarkTheme(dark: Boolean) {
    _isDarkTheme.value = dark
    prefs?.edit()?.putBoolean("dark_mode", dark)?.apply()
  }

  fun toggle() {
    setDarkTheme(!_isDarkTheme.value)
  }
}

private fun AppTheme.toColorScheme() = if (isDark) {
  darkColorScheme(
    primary = Color(colors.primary),
    onPrimary = Color(colors.onPrimary),
    secondary = Color(colors.secondary),
    onSecondary = Color(colors.onSecondary),
    tertiary = Color(colors.tertiary),
    background = Color(colors.background),
    onBackground = Color(colors.onBackground),
    surface = Color(colors.surface),
    onSurface = Color(colors.onSurface),
    surfaceVariant = Color(colors.surfaceVariant),
    onSurfaceVariant = Color(colors.onSurfaceVariant),
    error = Color(colors.error),
    outline = Color(colors.outline),
    outlineVariant = Color(colors.outlineVariant),
  )
} else {
  lightColorScheme(
    primary = Color(colors.primary),
    onPrimary = Color(colors.onPrimary),
    secondary = Color(colors.secondary),
    onSecondary = Color(colors.onSecondary),
    tertiary = Color(colors.tertiary),
    background = Color(colors.background),
    onBackground = Color(colors.onBackground),
    surface = Color(colors.surface),
    onSurface = Color(colors.onSurface),
    surfaceVariant = Color(colors.surfaceVariant),
    onSurfaceVariant = Color(colors.onSurfaceVariant),
    error = Color(colors.error),
    outline = Color(colors.outline),
    outlineVariant = Color(colors.outlineVariant),
  )
}

@Composable
fun StuffLocateTheme(
  content: @Composable () -> Unit,
) {
  val themeManager = remember { ServiceLocator.getAppThemeManager() }
  val persistedTheme by themeManager.currentThemeFlow.collectAsState()

  val colorScheme = remember(persistedTheme) { persistedTheme.toColorScheme() }

  IOSColors.updateFromTheme(persistedTheme)

  CompositionLocalProvider(LocalAppTheme provides persistedTheme) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content,
    )
  }
}
