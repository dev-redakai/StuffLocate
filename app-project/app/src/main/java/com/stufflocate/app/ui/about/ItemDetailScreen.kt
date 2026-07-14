package com.stufflocate.app.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.stufflocate.app.EditItem
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.theme.LiquidGlassCard
import com.stufflocate.app.theme.LiquidGlassTopBar
import com.stufflocate.app.theme.LocalAppTheme
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
  itemId: String,
  roomId: String,
  onBack: () -> Unit,
  onEdit: (EditItem) -> Unit,
) {
  val theme = LocalAppTheme.current
  val primaryColor = Color(theme.colors.primary)
  val backgroundColor = Color(theme.colors.background)
  val onSurfaceColor = Color(theme.colors.onSurface)
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)
  val scope = rememberCoroutineScope()

  var item by remember { mutableStateOf<Item?>(null) }
  var showFullPhoto by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(itemId) {
    item = ServiceLocator.getRepository().getItemById(itemId)
  }

  Scaffold(
    containerColor = backgroundColor,
    topBar = {
      LiquidGlassTopBar(
        title = item?.name ?: "Item Details",
        theme = theme,
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceColor)
          }
        },
        actions = {
          IconButton(onClick = { item?.let { onEdit(EditItem(it.id, it.roomId)) } }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = primaryColor)
          }
        },
      )
    },
  ) { padding ->
    if (item == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = primaryColor)
      }
    } else {
      val currentItem = item!!
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        // Item Photos
        if (currentItem.imagePaths.isNotEmpty()) {
          LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Photos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = primaryColor)
              Spacer(Modifier.height(8.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(currentItem.imagePaths) { index, path ->
                  val file = File(path)
                  if (file.exists()) {
                    Image(
                      painter = rememberAsyncImagePainter(file),
                      contentDescription = "Photo ${index + 1}",
                      modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showFullPhoto = path },
                      contentScale = ContentScale.Crop,
                    )
                  }
                }
              }
            }
          }
        }

        // Location Photos
        if (currentItem.locationPhotoPaths.isNotEmpty()) {
          LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Location Photos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = primaryColor)
              Spacer(Modifier.height(8.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(currentItem.locationPhotoPaths) { index, path ->
                  val file = File(path)
                  if (file.exists()) {
                    Image(
                      painter = rememberAsyncImagePainter(file),
                      contentDescription = "Location photo ${index + 1}",
                      modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showFullPhoto = path },
                      contentScale = ContentScale.Crop,
                    )
                  }
                }
              }
            }
          }
        }

        // Item Info
        LiquidGlassCard(modifier = Modifier.fillMaxWidth(), theme = theme) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailRow("Name", currentItem.name, onSurfaceColor, onSurfaceVariantColor)
            currentItem.category?.let { DetailRow("Category", it, onSurfaceColor, onSurfaceVariantColor) }
            DetailRow("Quantity", "${currentItem.quantity}", onSurfaceColor, onSurfaceVariantColor)
            DetailRow("Status", currentItem.status.name, onSurfaceColor, onSurfaceVariantColor)
            if (currentItem.tags.isNotEmpty()) {
              DetailRow("Tags", currentItem.tags.joinToString(", "), onSurfaceColor, onSurfaceVariantColor)
            }
            currentItem.notes?.let { DetailRow("Notes", it, onSurfaceColor, onSurfaceVariantColor) }
            currentItem.locationDescription?.let { DetailRow("Location", it, onSurfaceColor, onSurfaceVariantColor) }
          }
        }

        Spacer(Modifier.height(16.dp))
      }
    }
  }

  // Full screen photo viewer
  if (showFullPhoto != null) {
    Dialog(
      onDismissRequest = { showFullPhoto = null },
      properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.95f))
          .clickable { showFullPhoto = null },
        contentAlignment = Alignment.Center,
      ) {
        val file = File(showFullPhoto!!)
        if (file.exists()) {
          Image(
            painter = rememberAsyncImagePainter(file),
            contentDescription = "Full size photo",
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
              .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit,
          )
        }
        IconButton(
          onClick = { showFullPhoto = null },
          modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
        }
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String, labelColor: Color, valueColor: Color) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(label, style = MaterialTheme.typography.bodyMedium, color = labelColor.copy(alpha = 0.6f), modifier = Modifier.width(90.dp))
    Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Medium)
  }
}
