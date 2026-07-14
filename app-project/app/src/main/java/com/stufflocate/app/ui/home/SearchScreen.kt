package com.stufflocate.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.SearchOff
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
import com.stufflocate.app.EditItem
import com.stufflocate.app.ViewItem
import com.stufflocate.app.RoomDetail
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Categories
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.domain.model.SearchResult
import com.stufflocate.app.ui.common.CategoryChip
import com.stufflocate.app.ui.common.EmptyStateView
import com.stufflocate.app.ui.common.IOSColors
import com.stufflocate.app.ui.common.ModernBadge
import com.stufflocate.app.ui.common.ModernCard
import com.stufflocate.app.ui.common.RoundIconBox
import com.stufflocate.app.ui.common.StatusDropdown
import com.stufflocate.app.ui.common.StyledTextField
import com.stufflocate.app.ui.common.SwipeableItemCard
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
  onBack: () -> Unit,
  onNavigate: (NavKey) -> Unit,
  viewModel: SearchViewModel = viewModel {
    SearchViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Search Items", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
    ) {
      Spacer(Modifier.height(8.dp))

      // Search input
      StyledTextField(
        value = state.query,
        onValueChange = viewModel::updateQuery,
        placeholder = { Text("Search by name, category, or notes...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
          if (state.query.isNotBlank()) {
            IconButton(onClick = { viewModel.updateQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(Modifier.height(12.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { FilterChip(onClick = { viewModel.toggleCategory(null) },
          label = { Text("All") }, selected = state.selectedCategory == null) }
        items(Categories.ALL) { category ->
          CategoryChip(category = category, isSelected = state.selectedCategory == category,
            onClick = { viewModel.toggleCategory(category) }) }
      }

      Spacer(Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { FilterChip(onClick = { viewModel.toggleStatus(null) },
          label = { Text("All Status") }, selected = state.selectedStatus == null,
          leadingIcon = if (state.selectedStatus == null)
            {{ Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }} else null) }
        items(ItemStatus.entries) { status ->
          FilterChip(onClick = { viewModel.toggleStatus(status) },
            label = { Text(status.displayName) }, selected = state.selectedStatus == status,
            leadingIcon = if (state.selectedStatus == status)
              {{ Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }} else null) }
      }

      Spacer(Modifier.height(12.dp))
      HorizontalDivider()
      Spacer(Modifier.height(8.dp))

      when {
        state.isSearching -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = IOSColors.Primary) }
        state.query.isBlank() && state.selectedCategory == null && state.selectedStatus == null ->
          EmptyStateView(
            icon = { RoundIconBox(
              icon = { Icon(Icons.Default.Search, contentDescription = null, tint = IOSColors.Primary, modifier = Modifier.size(28.dp)) },
              size = 64.dp, color = IOSColors.Primary) },
            title = "Search your items",
            subtitle = "Type a name, select a category, or pick a status.",
          )
        state.results.isEmpty() -> EmptyStateView(
          icon = { RoundIconBox(
            icon = { Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = IOSColors.Secondary, modifier = Modifier.size(28.dp)) },
            size = 64.dp, color = IOSColors.Secondary) },
          title = "No items found",
          subtitle = "Try a different search term or category.",
        )
        else -> {
          Text("${state.results.size} result(s)", style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
          Spacer(Modifier.height(8.dp))
          LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.results, key = { it.item.id }) { result ->
              SearchResultItem(
                result = result,
                onClick = { onNavigate(ViewItem(result.item.id, result.roomId)) },
                onEdit = { onNavigate(EditItem(result.item.id, result.roomId)) },
                onDelete = { viewModel.deleteItem(result.item.id) },
                onStatusChange = { viewModel.updateItemStatus(result.item.id, it) },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SearchResultItem(
  result: SearchResult,
  onClick: () -> Unit,
  onEdit: () -> Unit = {},
  onDelete: () -> Unit = {},
  onStatusChange: (ItemStatus) -> Unit = {},
  modifier: Modifier = Modifier,
) {
  SwipeableItemCard(
    onEdit = onEdit,
    onDelete = onDelete,
    modifier = modifier,
  ) {
    Card(
      onClick = onClick,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.Top,
      ) {
        // Location photo or default icon
        if (result.locationPhotoPaths.isNotEmpty()) {
          val photoFile = File(result.locationPhotoPaths.first())
          if (photoFile.exists()) {
            Image(
              painter = rememberAsyncImagePainter(model = photoFile),
              contentDescription = "Location photo",
              modifier = Modifier.size(48.dp)
                .clip(RoundedCornerShape(12.dp)),
              contentScale = ContentScale.Crop,
            )
          } else {
            RoundIconBox(
              icon = { Icon(Icons.Default.Inventory, contentDescription = null,
                tint = IOSColors.Primary, modifier = Modifier.size(20.dp)) },
              size = 48.dp, color = IOSColors.Primary,
            )
          }
        } else {
          RoundIconBox(
            icon = { Icon(Icons.Default.Inventory, contentDescription = null,
              tint = IOSColors.Primary, modifier = Modifier.size(20.dp)) },
            size = 48.dp, color = IOSColors.Primary,
          )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(result.item.name, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Spacer(Modifier.height(4.dp))
          LocationPath(homeName = result.homeName, floorName = result.floorName, roomName = result.roomName)
          Spacer(Modifier.height(4.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (result.item.category != null) {
              ModernBadge(result.item.category.lowercase().replaceFirstChar { it.uppercase() }, color = IOSColors.Teal)
            }
            if (result.item.quantity > 1) {
              Text("×${result.item.quantity}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusDropdown(currentStatus = result.item.status, onStatusChange = onStatusChange)
          }
          // Show location photo count if available
          if (result.locationPhotoPaths.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CameraAlt, contentDescription = null,
                tint = IOSColors.Orange, modifier = Modifier.size(12.dp))
              Spacer(Modifier.width(4.dp))
              Text("${result.locationPhotoPaths.size} location photo(s)",
                style = MaterialTheme.typography.labelSmall, color = IOSColors.Orange)
            }
          }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Edit item", tint = IOSColors.Primary, modifier = Modifier.size(18.dp))
          }
          Icon(Icons.Default.ChevronRight, contentDescription = "Go to room",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
        }
      }
    }
  }
}

@Composable
private fun LocationPath(homeName: String, floorName: String, roomName: String) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(12.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
    Text(homeName, style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(10.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    Text(floorName, style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(10.dp),
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    Text(roomName, style = MaterialTheme.typography.labelSmall,
      color = IOSColors.Primary, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
  }
}
