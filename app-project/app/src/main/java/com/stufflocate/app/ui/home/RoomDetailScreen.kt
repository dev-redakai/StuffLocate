package com.stufflocate.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import coil.compose.rememberAsyncImagePainter
import com.stufflocate.app.CreateItem
import com.stufflocate.app.EditItem
import com.stufflocate.app.ViewItem
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.ui.common.EmptyStateView
import com.stufflocate.app.ui.common.IOSColors
import com.stufflocate.app.ui.common.ModernBadge
import com.stufflocate.app.ui.common.ModernCard
import com.stufflocate.app.ui.common.RoundIconBox
import com.stufflocate.app.ui.common.StatusDropdown
import com.stufflocate.app.ui.common.SwipeableItemCard
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
  roomId: String,
  roomName: String,
  onNavigate: (NavKey) -> Unit,
  onBack: () -> Unit,
  viewModel: RoomDetailViewModel = viewModel {
    RoomDetailViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(roomId) { viewModel.loadItems(roomId) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(roomName, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { onNavigate(CreateItem(roomId, roomName)) }) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
          }
        },
      )
    },
  ) { padding ->
    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
      when {
        state.isLoading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = IOSColors.Primary)
          }
        }
        state.error != null -> {
          Text("Error: ${state.error?.message}", modifier = Modifier.padding(16.dp),
            color = IOSColors.Red)
        }
        else -> {
          if (state.items.isEmpty()) {
            EmptyStateView(
              icon = {
                RoundIconBox(
                  icon = { Icon(Icons.Outlined.Inventory2, contentDescription = null,
                    tint = IOSColors.Primary, modifier = Modifier.size(32.dp)) },
                  size = 72.dp, color = IOSColors.Primary,
                )
              },
              title = "No items in $roomName",
              subtitle = "Start adding items to keep track of your belongings.",
              action = {
                Button(onClick = { onNavigate(CreateItem(roomId, roomName)) },
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Primary)) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(Modifier.width(8.dp))
                  Text("Add Item")
                }
              },
            )
          } else {
            LazyColumn(
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text("${state.items.size} item(s)", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
              }

              items(state.items, key = { it.id }) { item ->
                ItemCard(
                  item = item,
                  onView = { onNavigate(ViewItem(item.id, roomId)) },
                  onEdit = { onNavigate(EditItem(item.id, roomId)) },
                  onDelete = { viewModel.deleteItem(item.id, roomId) },
                  onStatusChange = { viewModel.updateItemStatus(item.id, it) },
                )
              }

              item { Spacer(Modifier.height(80.dp)) }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ItemCard(
  item: Item,
  onView: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onStatusChange: (ItemStatus) -> Unit = {},
  modifier: Modifier = Modifier,
) {
  SwipeableItemCard(
    onEdit = onEdit,
    onDelete = onDelete,
    modifier = modifier,
  ) {
    ModernCard(modifier = Modifier.fillMaxWidth().clickable { onView() }, shape = RoundedCornerShape(16.dp), elevation = 1.dp) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Show location photo if available, else default icon
        if (item.locationPhotoPaths.isNotEmpty()) {
          val photoFile = File(item.locationPhotoPaths.first())
          if (photoFile.exists()) {
            Image(
              painter = rememberAsyncImagePainter(model = photoFile),
              contentDescription = "Location photo",
              modifier = Modifier.size(42.dp)
                .clip(RoundedCornerShape(12.dp)),
              contentScale = ContentScale.Crop,
            )
          } else {
            RoundIconBox(
              icon = { Icon(Icons.Default.Inventory, contentDescription = null,
                tint = IOSColors.Primary, modifier = Modifier.size(20.dp)) },
              size = 42.dp, color = IOSColors.Primary,
            )
          }
        } else {
          RoundIconBox(
            icon = { Icon(Icons.Default.Inventory, contentDescription = null,
              tint = IOSColors.Primary, modifier = Modifier.size(20.dp)) },
            size = 42.dp, color = IOSColors.Primary,
          )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(item.name, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (item.category != null) {
              ModernBadge(item.category.lowercase().replaceFirstChar { it.uppercase() },
                color = IOSColors.Teal)
            }
            if (item.quantity > 1) {
              Text("×${item.quantity}", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusDropdown(currentStatus = item.status, onStatusChange = onStatusChange)
          }
          if (item.locationPhotoPaths.isNotEmpty()) {
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CameraAlt, contentDescription = null,
                tint = IOSColors.Orange, modifier = Modifier.size(10.dp))
              Spacer(Modifier.width(3.dp))
              Text("${item.locationPhotoPaths.size} location photo(s)",
                style = MaterialTheme.typography.labelSmall, color = IOSColors.Orange)
            }
          }
        }
        if (item.notes != null) {
          Icon(Icons.Default.Notes, contentDescription = "Has notes",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(4.dp))
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit item",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Delete, contentDescription = "Delete",
            tint = IOSColors.Red.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
