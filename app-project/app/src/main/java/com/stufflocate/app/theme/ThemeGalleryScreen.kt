package com.stufflocate.app.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeGalleryScreen(
  themeManager: AppThemeManager,
  onBack: () -> Unit,
  onThemeSelected: (AppTheme) -> Unit,
  onCreateCustom: () -> Unit,
  onImportTheme: () -> Unit,
) {
  var showDeleteDialog by remember { mutableStateOf(false) }
  var deletingThemeId by remember { mutableStateOf("") }

  LiquidGlassScaffold(
    topBar = {
      LiquidGlassTopBar(
        title = "Themes",
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
    floatingActionButton = {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        SmallFloatingActionButton(
          onClick = onImportTheme,
          containerColor = Color(LocalAppTheme.current.colors.secondary),
          contentColor = Color.White,
        ) {
          Icon(Icons.Default.FileUpload, contentDescription = "Import Theme")
        }
        FloatingActionButton(
          onClick = onCreateCustom,
          containerColor = Color(LocalAppTheme.current.colors.primary),
          contentColor = Color.White,
        ) {
          Icon(Icons.Default.Add, contentDescription = "Create Theme")
        }
      }
    },
  ) { padding ->
    val currentTheme by themeManager.currentThemeFlow.collectAsState()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      // Current Theme
      LiquidGlassCard {
        Text("Current Theme", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))
        ThemePreviewCard(
          theme = currentTheme,
          isCurrent = true,
          onClick = {},
        )
      }

      // Preset Themes
      LiquidGlassCard {
        Text("Preset Themes", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))

          ThemePresets.ALL.chunked(2).forEachIndexed { chunkIdx, rowThemes ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            rowThemes.forEach { presetTheme ->
              ThemePreviewCard(
                theme = presetTheme,
                isCurrent = presetTheme.id == currentTheme.id,
                onClick = {
                  themeManager.setPresetTheme(presetTheme)
                  ThemeManager.setDarkTheme(presetTheme.isDark)
                },
                modifier = Modifier.weight(1f),
              )
            }
            if (rowThemes.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
          if (chunkIdx < ThemePresets.ALL.chunked(2).lastIndex) {
            Spacer(modifier = Modifier.height(12.dp))
          }
        }
      }

      // Custom Themes
      if (themeManager.customThemes.isNotEmpty()) {
        LiquidGlassCard {
          Text("Custom Themes", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))

          themeManager.customThemes.chunked(2).forEachIndexed { chunkIdx, rowThemes ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              rowThemes.forEach { customTheme ->
                ThemePreviewCard(
                  theme = customTheme,
                  isCurrent = customTheme.id == currentTheme.id,
                  onClick = {
                    themeManager.setPresetTheme(customTheme)
                    ThemeManager.setDarkTheme(customTheme.isDark)
                  },
                  onDelete = {
                    deletingThemeId = customTheme.id
                    showDeleteDialog = true
                  },
                  modifier = Modifier.weight(1f),
                )
              }
              if (rowThemes.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
            if (chunkIdx < themeManager.customThemes.chunked(2).lastIndex) {
              Spacer(modifier = Modifier.height(12.dp))
            }
          }
        }
      }
    }
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      containerColor = Color.White,
      shape = RoundedCornerShape(24.dp),
      title = { Text("Delete Theme?", fontWeight = FontWeight.Bold) },
      text = { Text("This action cannot be undone.") },
      confirmButton = {
        LiquidGlassButton(
          text = "Delete",
          onClick = {
            themeManager.deleteCustomTheme(deletingThemeId)
            showDeleteDialog = false
          },
          style = ButtonStyle.FILLED,
        )
      },
      dismissButton = {
        LiquidGlassButton(
          text = "Cancel",
          onClick = { showDeleteDialog = false },
          style = ButtonStyle.GLASS,
        )
      },
    )
  }
}

@Composable
private fun ThemePreviewCard(
  theme: AppTheme,
  isCurrent: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onDelete: (() -> Unit)? = null,
) {
  val primaryColor = Color(theme.colors.primary)
  val secondaryColor = Color(theme.colors.secondary)
  val tertiaryColor = Color(theme.colors.tertiary)
  val backgroundColor = Color(theme.colors.background)
  val surfaceColor = Color(theme.colors.surface)

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .then(
        if (isCurrent) Modifier.border(2.dp, primaryColor, RoundedCornerShape(20.dp))
        else Modifier
      ),
  ) {
    // Color preview
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              backgroundColor,
              surfaceColor,
              primaryColor.copy(alpha = 0.2f),
            )
          )
        )
        .padding(12.dp),
    ) {
      // Mini UI elements preview
      Column {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(primaryColor),
          )
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(secondaryColor),
          )
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(tertiaryColor),
          )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Box(
            modifier = Modifier
              .weight(0.6f)
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(primaryColor.copy(alpha = 0.3f)),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .weight(0.3f)
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(secondaryColor.copy(alpha = 0.3f)),
          )
        }
      }

      if (isCurrent) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(24.dp)
            .clip(CircleShape)
            .background(primaryColor),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            Icons.Default.Check,
            contentDescription = "Current",
            tint = Color.White,
            modifier = Modifier.size(14.dp),
          )
        }
      }

      if (onDelete != null) {
        IconButton(
          onClick = onDelete,
          modifier = Modifier.align(Alignment.TopStart).size(24.dp),
        ) {
          Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color(theme.colors.error),
            modifier = Modifier.size(14.dp),
          )
        }
      }
    }

    // Theme name
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(surfaceColor)
        .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
      Text(
        theme.name,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = Color(theme.colors.onSurface),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}

@Composable
fun ThemeImportScreen(
  themeManager: AppThemeManager,
  onBack: () -> Unit,
  onImported: (AppTheme) -> Unit,
) {
  var jsonInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }

  LiquidGlassScaffold(
    topBar = {
      LiquidGlassTopBar(
        title = "Import Theme",
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      LiquidGlassCard {
        Text("Import from JSON", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        Text(
          "Paste a theme JSON string below to import it into your theme library.",
          style = MaterialTheme.typography.bodySmall,
          color = Color(LocalAppTheme.current.colors.onSurfaceVariant),
        )
      }

      LiquidGlassTextField(
        value = jsonInput,
        onValueChange = { jsonInput = it; errorMessage = null; successMessage = null },
        label = "Theme JSON",
        placeholder = """{"name": "My Theme", ...}""",
      )

      if (errorMessage != null) {
        LiquidGlassCard {
          Text(
            errorMessage!!,
            color = Color(LocalAppTheme.current.colors.error),
            style = MaterialTheme.typography.bodySmall,
          )
        }
      }

      if (successMessage != null) {
        LiquidGlassCard {
          Text(
            successMessage!!,
            color = Color(LocalAppTheme.current.colors.success),
            style = MaterialTheme.typography.bodySmall,
          )
        }
      }

      LiquidGlassButton(
        text = "Import Theme",
        onClick = {
          if (jsonInput.isBlank()) {
            errorMessage = "Please paste a theme JSON string"
            return@LiquidGlassButton
          }
          val imported = themeManager.importTheme(jsonInput)
          if (imported != null) {
            successMessage = "Theme '${imported.name}' imported successfully!"
            onImported(imported)
          } else {
            errorMessage = "Invalid theme JSON. Please check the format."
          }
        },
        modifier = Modifier.fillMaxWidth(),
        icon = { Icon(Icons.Default.FileUpload, contentDescription = null) },
      )

      LiquidGlassButton(
        text = "Export Current Theme",
        onClick = {
          jsonInput = themeManager.exportTheme(themeManager.currentTheme)
        },
        modifier = Modifier.fillMaxWidth(),
        style = ButtonStyle.GLASS,
        icon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
      )
    }
  }
}

@Composable
fun LiquidGlassScaffold(
  topBar: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  floatingActionButton: @Composable () -> Unit = {},
  content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
    topBar = topBar,
    floatingActionButton = floatingActionButton,
    containerColor = Color(LocalAppTheme.current.colors.background),
    contentColor = Color(LocalAppTheme.current.colors.onBackground),
    content = content,
  )
}
