package com.stufflocate.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.stufflocate.app.CreateFloor
import com.stufflocate.app.CreateRoom
import com.stufflocate.app.FloorEdit
import com.stufflocate.app.FloorPlanEditor
import com.stufflocate.app.HomeEdit
import com.stufflocate.app.RoomDetail
import com.stufflocate.app.RoomEdit
import com.stufflocate.app.domain.model.RoomModel
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Floor
import com.stufflocate.app.ui.common.CollapsibleSection
import com.stufflocate.app.ui.common.IOSColors
import com.stufflocate.app.ui.common.ModernBadge
import com.stufflocate.app.ui.common.ModernCard
import com.stufflocate.app.ui.common.ModernCardRow
import com.stufflocate.app.ui.common.RoundIconBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDetailScreen(
  homeId: String,
  onNavigate: (NavKey) -> Unit,
  onBack: () -> Unit,
  viewModel: HomeDetailViewModel = viewModel {
    HomeDetailViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val home = state.home

  LaunchedEffect(homeId) { viewModel.loadHome(homeId) }
  LaunchedEffect(state.homeDeleted) { if (state.homeDeleted) onBack() }

  state.deleteConfirm?.let { target ->
    AlertDialog(
      onDismissRequest = { viewModel.cancelDelete() },
      title = { Text("Delete ${target.name}?", fontWeight = FontWeight.Bold) },
      text = { Text("This will permanently delete ${target.name} and all its contents.") },
      confirmButton = {
        Button(onClick = { viewModel.confirmDelete() },
          colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Red),
          shape = RoundedCornerShape(12.dp)) { Text("Delete") }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.cancelDelete() }) { Text("Cancel") }
      },
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(home?.name ?: "Home", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (home != null) {
            IconButton(onClick = { onNavigate(HomeEdit(homeId)) }) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Home",
                tint = IOSColors.Primary)
            }
            IconButton(onClick = { onNavigate(FloorPlanEditor(homeId, homeId)) }) {
              Icon(Icons.Default.Draw, contentDescription = "Draw Floor Plan",
                tint = IOSColors.Secondary)
            }
            IconButton(onClick = { viewModel.requestDeleteHome(homeId) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete Home",
                tint = IOSColors.Red)
            }
          }
          IconButton(onClick = { onNavigate(CreateFloor(homeId)) }) {
            Icon(Icons.Default.Add, contentDescription = "Add Floor")
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
          Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            Text("Error: ${state.error?.message}", color = IOSColors.Red)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.loadHome(homeId) },
              shape = RoundedCornerShape(12.dp)) { Text("Retry") }
          }
        }
        home != null -> {
          if (home.floors.isEmpty()) {
            Column(
              modifier = Modifier.fillMaxSize().padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
            ) {
              RoundIconBox(
                icon = { Icon(Icons.Default.Layers, contentDescription = null,
                  tint = IOSColors.Primary, modifier = Modifier.size(32.dp)) },
                size = 72.dp, color = IOSColors.Primary,
              )
              Spacer(Modifier.height(16.dp))
              Text("No floors yet", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
              Spacer(Modifier.height(8.dp))
              Text("Add your first floor to start organizing this home.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(Modifier.height(16.dp))
              Button(onClick = { onNavigate(CreateFloor(homeId)) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Primary)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add Floor")
              }
            }
          } else {
            LazyColumn(
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              // Home Info Card
              item {
                ModernCard(shape = RoundedCornerShape(20.dp), elevation = 1.dp) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(home.name, style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                      if (!home.address.isNullOrBlank()) {
                        Text(home.address, style = MaterialTheme.typography.bodyMedium,
                          color = MaterialTheme.colorScheme.onSurfaceVariant)
                      }
                    }
                    IconButton(onClick = { viewModel.requestDeleteHome(homeId) }) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete Home",
                        tint = IOSColors.Red.copy(alpha = 0.6f))
                    }
                  }
                  Spacer(Modifier.height(4.dp))
                  ModernBadge(
                    "${home.floors.size} floor(s) • ${home.floors.sumOf { it.rooms.size }} room(s)",
                    color = IOSColors.Primary)
                }
              }

              items(home.floors, key = { it.id }) { floor ->
                FloorCard(
                  floor = floor,
                  onAddRoom = { onNavigate(CreateRoom(floor.id, homeId)) },
                  onRoomClick = { room -> onNavigate(RoomDetail(room.id, room.name, floor.id)) },
                  onEditFloor = { onNavigate(FloorEdit(floor.id, homeId)) },
                  onEditRoom = { room -> onNavigate(RoomEdit(room.id, floor.id)) },
                  onDeleteFloor = { viewModel.requestDeleteFloor(floor.id, floor.name) },
                  onDeleteRoom = { room -> viewModel.requestDeleteRoom(room.id, room.name) },
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
private fun FloorCard(
  floor: Floor,
  onAddRoom: () -> Unit,
  onRoomClick: (RoomModel) -> Unit,
  onEditFloor: () -> Unit,
  onEditRoom: (RoomModel) -> Unit,
  onDeleteFloor: () -> Unit,
  onDeleteRoom: (RoomModel) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showMenu by remember { mutableStateOf(false) }

  CollapsibleSection(
    title = floor.name,
    subtitle = "Floor ${floor.floorNumber}",
    count = floor.rooms.size,
    defaultExpanded = true,
    headerColor = IOSColors.Indigo,
    modifier = modifier,
    trailing = {
      Row {
        Box {
          IconButton(onClick = { showMenu = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Floor options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
              text = { Text("Edit Floor") },
              onClick = { showMenu = false; onEditFloor() },
              leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = IOSColors.Primary) },
            )
            DropdownMenuItem(
              text = { Text("Delete Floor", color = IOSColors.Red) },
              onClick = { showMenu = false; onDeleteFloor() },
              leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = IOSColors.Red) },
            )
          }
        }
        IconButton(onClick = onAddRoom) {
          Icon(Icons.Default.Add, contentDescription = "Add Room", tint = IOSColors.Primary)
        }
      }
    },
  ) {
    if (floor.rooms.isEmpty()) {
      Text("No rooms yet. Tap + to add one.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp))
    } else {
      floor.rooms.forEach { room ->
        RoomRow(
          room = room,
          onClick = { onRoomClick(room) },
          onEdit = { onEditRoom(room) },
          onDelete = { onDeleteRoom(room) },
        )
        if (room != floor.rooms.last()) Spacer(Modifier.height(6.dp))
      }
    }
  }
}

@Composable
private fun RoomRow(
  room: RoomModel,
  onClick: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var showMenu by remember { mutableStateOf(false) }

  Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      RoundIconBox(
        icon = { Icon(Icons.Default.MeetingRoom, contentDescription = null,
          tint = IOSColors.Primary, modifier = Modifier.size(18.dp)) },
        size = 36.dp, color = IOSColors.Primary,
      )
      Spacer(Modifier.width(12.dp))
      Text(room.name, style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
      if (room.itemCount > 0) {
        ModernBadge("${room.itemCount} items", color = IOSColors.Green)
        Spacer(Modifier.width(4.dp))
      }
      // Room menu
      Box {
        IconButton(onClick = { showMenu = true }) {
          Icon(Icons.Default.MoreVert, contentDescription = "Room options",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp))
        }
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
          DropdownMenuItem(
            text = { Text("Edit") },
            onClick = { showMenu = false; onEdit() },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = IOSColors.Primary) },
          )
          DropdownMenuItem(
            text = { Text("Delete", color = IOSColors.Red) },
            onClick = { showMenu = false; onDelete() },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = IOSColors.Red) },
          )
        }
      }
    }
  }
}
