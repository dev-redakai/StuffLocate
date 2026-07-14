package com.stufflocate.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.stufflocate.app.EditItem
import com.stufflocate.app.ViewItem
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.ui.common.EmptyStateView
import com.stufflocate.app.ui.common.IOSColors
import com.stufflocate.app.ui.common.ModernBadge
import com.stufflocate.app.ui.common.ModernCard
import com.stufflocate.app.ui.common.RoundIconBox
import com.stufflocate.app.ui.common.StatusDropdown
import com.stufflocate.app.ui.common.SwipeableItemCard
import com.stufflocate.app.domain.model.ItemStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllItemsScreen(
  onBack: () -> Unit,
  onNavigate: (NavKey) -> Unit = {},
  viewModel: AllItemsViewModel = viewModel {
    AllItemsViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("All Items", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
      when {
        state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = IOSColors.Primary) }
        state.items.isEmpty() -> EmptyStateView(
          icon = { RoundIconBox(
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = IOSColors.Primary, modifier = Modifier.size(32.dp)) },
            size = 72.dp, color = IOSColors.Primary) },
          title = "No items yet",
          subtitle = "Add items to rooms to see them all here.",
        )
        else -> {
          LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text("${state.items.size} item(s)", style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(shape = RoundedCornerShape(10.dp), color = IOSColors.Primary.copy(alpha = 0.1f)) {
                  Text("${state.totalQuantity} units", style = MaterialTheme.typography.labelMedium,
                    color = IOSColors.Primary, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
              }
            }
            items(state.items, key = { it.id }) { item ->
              AllItemsCard(
                item = item,
                onView = { onNavigate(ViewItem(item.id, item.roomId)) },
                onEdit = { onNavigate(EditItem(item.id, item.roomId)) },
                onDelete = { viewModel.deleteItem(item.id) },
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

@Composable
private fun AllItemsCard(
  item: Item,
  onView: () -> Unit = {},
  onEdit: () -> Unit = {},
  onDelete: () -> Unit = {},
  onStatusChange: (ItemStatus) -> Unit = {},
  modifier: Modifier = Modifier,
) {
  SwipeableItemCard(
    onEdit = onEdit,
    onDelete = onDelete,
    onTap = onView,
    modifier = modifier,
  ) {
    ModernCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = 1.dp) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        RoundIconBox(
          icon = { Icon(Icons.Default.Inventory, contentDescription = null,
            tint = IOSColors.Primary, modifier = Modifier.size(20.dp)) },
          size = 42.dp, color = IOSColors.Primary,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(item.name, style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (item.category != null) {
              ModernBadge(item.category.lowercase().replaceFirstChar { it.uppercase() }, color = IOSColors.Teal)
            }
            if (item.quantity > 1) {
              Text("×${item.quantity}", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusDropdown(currentStatus = item.status, onStatusChange = onStatusChange)
          }
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Edit, contentDescription = "Edit item",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
