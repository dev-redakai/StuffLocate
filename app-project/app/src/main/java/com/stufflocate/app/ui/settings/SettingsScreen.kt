package com.stufflocate.app.ui.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  onBack: () -> Unit,
  onNavigateToThemes: () -> Unit = {},
  onNavigateToAbout: () -> Unit = {},
  viewModel: SettingsViewModel = viewModel {
    SettingsViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val isDark by ThemeManager.isDarkTheme.collectAsState()
  val context = LocalContext.current
  var showShareDialog by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }
  val themeManager = remember { ServiceLocator.getAppThemeManager() }
  val currentTheme = themeManager.currentTheme
  val primaryColor = Color(currentTheme.colors.primary)
  val secondaryColor = Color(currentTheme.colors.secondary)
  val backgroundColor = Color(currentTheme.colors.background)
  val onSurfaceColor = Color(currentTheme.colors.onSurface)
  val onSurfaceVariantColor = Color(currentTheme.colors.onSurfaceVariant)
  val surfaceColor = Color(currentTheme.colors.surface)

  LaunchedEffect(state.importMessage) {
    state.importMessage?.let {
      snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
      viewModel.clearImportMessage()
    }
  }

  Scaffold(
    containerColor = backgroundColor,
    topBar = {
      LiquidGlassTopBar(
        title = "Settings",
        theme = currentTheme,
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceColor)
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState()),
    ) {
      // Gradient Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              colors = listOf(primaryColor, secondaryColor),
            ),
          )
          .padding(32.dp),
        contentAlignment = Alignment.Center,
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.2f),
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Home, contentDescription = null,
                modifier = Modifier.size(36.dp), tint = Color.White)
            }
          }
          Spacer(Modifier.height(12.dp))
          Text("Stuff Locate", style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = Color.White)
          Text("v1.0.0", style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f))
        }
      }

      Spacer(Modifier.height(16.dp))

      // Themes Section
      GlassSectionTitle("Themes", color = primaryColor)
      GlassSettingsGroup(currentTheme) {
        GlassSettingsItem(
          icon = Icons.Default.Palette,
          title = "Theme Gallery",
          subtitle = "Browse and apply ${themeManager.getAllThemes().size} themes",
          theme = currentTheme,
          onClick = onNavigateToThemes,
        )
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 16.dp),
          color = Color(currentTheme.colors.outline).copy(alpha = 0.3f),
        )
        GlassSettingsItem(
          icon = Icons.Default.ColorLens,
          title = "Current Theme",
          subtitle = currentTheme.name,
          theme = currentTheme,
          onClick = onNavigateToThemes,
        )
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 16.dp),
          color = Color(currentTheme.colors.outline).copy(alpha = 0.3f),
        )
        GlassSettingsToggleItem(
          icon = Icons.Default.DarkMode,
          title = "Dark Mode",
          subtitle = "Switch between light and dark mode",
          checked = isDark,
          onCheckedChange = { dark ->
            ThemeManager.setDarkTheme(dark)
            themeManager.currentTheme = themeManager.currentTheme.copy(isDark = dark)
          },
          theme = currentTheme,
        )
      }

      Spacer(Modifier.height(8.dp))

      // Preferences
      GlassSectionTitle("Preferences", color = primaryColor)
      GlassSettingsGroup(currentTheme) {
        GlassSettingsItem(
          icon = Icons.Default.Tune,
          title = "Glass Style",
          subtitle = when (currentTheme.glass.style) {
            GlassStyle.LIQUID -> "Liquid Glass"
            GlassStyle.FROSTED -> "Frosted Glass"
            GlassStyle.AURORA -> "Aurora Borealis"
            GlassStyle.NEON -> "Neon Glow"
            GlassStyle.MINIMAL -> "Minimal"
          },
          theme = currentTheme,
          onClick = onNavigateToThemes,
        )
      }

      Spacer(Modifier.height(8.dp))

      // Sharing
      GlassSectionTitle("Sharing", color = primaryColor)
      GlassSettingsGroup(currentTheme) {
        GlassSettingsItem(
          icon = Icons.Default.Share,
          title = "Export All Homes",
          subtitle = "Share your entire inventory as JSON",
          theme = currentTheme,
          onClick = {
            viewModel.exportAllHomes()
            context.startActivity(
              Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_TEXT, state.exportedData)
                  putExtra(Intent.EXTRA_SUBJECT, "Stuff Locate - Home Inventory")
                }, "Share Inventory"
              )
            )
          },
        )
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 16.dp),
          color = Color(currentTheme.colors.outline).copy(alpha = 0.3f),
        )
        GlassSettingsItem(
          icon = Icons.Default.FileUpload,
          title = "Import Inventory",
          subtitle = "Import homes from shared data",
          theme = currentTheme,
          onClick = { showShareDialog = true },
        )
      }

      Spacer(Modifier.height(8.dp))

      // About
      GlassSectionTitle("About", color = primaryColor)
      GlassSettingsGroup(currentTheme) {
        GlassSettingsItem(
          icon = Icons.Default.Info,
          title = "About Stuff Locate",
          subtitle = "Smart home storage organizer",
          theme = currentTheme,
          onClick = onNavigateToAbout,
        )
      }

      Spacer(Modifier.height(32.dp))
      Text(
        "Made with love for organized living",
        style = MaterialTheme.typography.bodySmall,
        color = onSurfaceVariantColor.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
        textAlign = TextAlign.Center,
      )
    }

    if (showShareDialog) {
      GlassImportDialog(
        onDismiss = { showShareDialog = false },
        onImport = { viewModel.importHomes(it); showShareDialog = false },
        theme = currentTheme,
      )
    }
  }
}

@Composable
private fun GlassSectionTitle(title: String, color: Color) {
  Text(
    title,
    style = MaterialTheme.typography.titleSmall,
    fontWeight = FontWeight.SemiBold,
    color = color,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  )
}

@Composable
private fun GlassSettingsGroup(
  theme: AppTheme,
  content: @Composable ColumnScope.() -> Unit,
) {
  LiquidGlassCard(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    theme = theme,
  ) {
    Column(modifier = Modifier.padding(vertical = 0.dp), content = content)
  }
}

@Composable
private fun GlassSettingsItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  theme: AppTheme,
  onClick: () -> Unit,
) {
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)

  LiquidGlassCard(
    modifier = Modifier.fillMaxWidth(),
    theme = theme,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        icon,
        contentDescription = null,
        tint = onSurfaceVariantColor,
        modifier = Modifier.size(24.dp),
      )
      Spacer(Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium,
          color = Color(theme.colors.onSurface))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = onSurfaceVariantColor)
      }
      Icon(
        Icons.Default.ChevronRight,
        contentDescription = null,
        tint = onSurfaceVariantColor.copy(alpha = 0.3f),
      )
    }
  }
}

@Composable
private fun GlassSettingsToggleItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  theme: AppTheme,
) {
  val primaryColor = Color(theme.colors.primary)
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)

  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      icon,
      contentDescription = null,
      tint = onSurfaceVariantColor,
      modifier = Modifier.size(24.dp),
    )
    Spacer(Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium,
        color = Color(theme.colors.onSurface))
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = onSurfaceVariantColor)
    }
    Spacer(Modifier.width(8.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = primaryColor,
        checkedTrackColor = primaryColor.copy(alpha = 0.3f),
      ),
    )
  }
}

@Composable
private fun GlassImportDialog(onDismiss: () -> Unit, onImport: (String) -> Unit, theme: AppTheme) {
  var text by remember { mutableStateOf("") }
  val primaryColor = Color(theme.colors.primary)
  val onSurfaceColor = Color(theme.colors.onSurface)
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(theme.colors.surface),
    titleContentColor = onSurfaceColor,
    textContentColor = onSurfaceVariantColor,
    title = { Text("Import Inventory", fontWeight = FontWeight.Bold) },
    text = {
      Column {
        Text(
          "Paste the shared JSON data below to import homes and items.",
          style = MaterialTheme.typography.bodyMedium,
          color = onSurfaceVariantColor,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
          value = text,
          onValueChange = { text = it },
          label = { Text("JSON Data") },
          modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color(theme.colors.outline),
            cursorColor = primaryColor,
          ),
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onImport(text) },
        enabled = text.isNotBlank(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
      ) { Text("Import") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}
