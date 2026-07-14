package com.stufflocate.app.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Categories
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.domain.model.RoomLocation
import com.stufflocate.app.ui.common.IOSColors
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ItemFormScreen(
  roomId: String,
  roomName: String,
  editItemId: String? = null,
  onBack: () -> Unit,
  onSaved: () -> Unit,
) {
  val context = LocalContext.current
  val app = context.applicationContext as android.app.Application
  val viewModel: ItemFormViewModel = viewModel {
    ItemFormViewModel(
      application = app,
      repository = ServiceLocator.getRepository(),
      roomId = roomId,
      editItemId = editItemId,
    )
  }
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val isEditing = editItemId != null
  var showRoomPicker by remember { mutableStateOf(false) }

  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    uri?.let { viewModel.addPhoto(it, isLocationPhoto = false) }
  }

  val locationGalleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    uri?.let { viewModel.addPhoto(it, isLocationPhoto = true) }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(if (isEditing) "Edit Item" else "Add Item", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Spacer(Modifier.height(4.dp))

      // Current room info
      if (isEditing) {
        Surface(
          onClick = { showRoomPicker = true },
          shape = RoundedCornerShape(14.dp),
          color = IOSColors.Primary.copy(alpha = 0.08f),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(Icons.Default.LocationOn, contentDescription = null,
              tint = IOSColors.Primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              val displayRoom = state.roomName.ifBlank { roomName }
              val displayFloor = state.floorName
              val displayHome = state.homeName
              Text(
                if (displayFloor.isNotBlank()) "$displayRoom · $displayFloor" else displayRoom,
                style = MaterialTheme.typography.bodyMedium,
                color = IOSColors.Primary, maxLines = 1, overflow = TextOverflow.Ellipsis,
              )
              if (displayHome.isNotBlank()) {
                Text(displayHome, style = MaterialTheme.typography.bodySmall,
                  color = IOSColors.Primary.copy(alpha = 0.6f))
              }
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.SwapHoriz, contentDescription = "Move to another room",
              tint = IOSColors.Primary, modifier = Modifier.size(20.dp))
          }
        }
      } else {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = IOSColors.Primary.copy(alpha = 0.08f),
        ) {
          Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null,
              tint = IOSColors.Primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Storing in: $roomName", style = MaterialTheme.typography.bodyMedium,
              color = IOSColors.Primary)
          }
        }
      }

      OutlinedTextField(value = state.name, onValueChange = viewModel::updateName,
        label = { Text("Item Name") }, placeholder = { Text("e.g., Winter Jacket, Passport") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))

      // Category
      var categoryExpanded by remember { mutableStateOf(false) }
      ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
        OutlinedTextField(
          value = state.category?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Select category",
          onValueChange = {}, readOnly = true, label = { Text("Category") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
          modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
          shape = RoundedCornerShape(14.dp))
        ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
          Categories.ALL.forEach { category ->
            DropdownMenuItem(
              text = { Text(category.lowercase().replaceFirstChar { it.uppercase() }) },
              onClick = { viewModel.updateCategory(category); categoryExpanded = false })
          }
        }
      }

      // Quantity + Status
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = state.quantity.toString(),
          onValueChange = { viewModel.updateQuantity(it.toIntOrNull() ?: 1) },
          label = { Text("Quantity") }, singleLine = true,
          modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp))

        var statusExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded },
          modifier = Modifier.weight(1f)) {
          OutlinedTextField(value = state.status.displayName, onValueChange = {},
            readOnly = true, label = { Text("Status") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            shape = RoundedCornerShape(14.dp))
          ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
            ItemStatus.entries.forEach { status ->
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(status)
                    Spacer(Modifier.width(8.dp))
                    Text(status.displayName)
                  }
                },
                onClick = { viewModel.updateStatus(status); statusExpanded = false })
            }
          }
        }
      }

      // Tags
      OutlinedTextField(value = state.tagsInput, onValueChange = viewModel::updateTagsInput,
        label = { Text("Tags") }, placeholder = { Text("Separate tags with commas") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        supportingText = { Text("Tags help you find items faster") })

      if (state.tags.isNotEmpty()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          state.tags.forEach { tag ->
            SuggestionChip(onClick = {}, label = { Text(tag, style = MaterialTheme.typography.labelSmall) })
          }
        }
      }

      // ─── Item Photos Section ─────────────────────────────
      SectionHeader(
        icon = Icons.Default.PhotoCamera,
        title = "Item Photos",
        subtitle = "${state.imagePaths.size}/5 photos",
      )

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(state.imagePaths) { index, path ->
          PhotoThumbnail(
            path = path,
            onClick = { viewModel.removePhoto(index, isLocationPhoto = false) },
          )
        }
        if (state.imagePaths.size < 5) {
          item {
            AddPhotoButton(
              onClick = { galleryLauncher.launch("image/*") },
              label = "Gallery",
            )
          }
        }
      }

      // ─── Location Photos Section ─────────────────────────
      SectionHeader(
        icon = Icons.Default.CameraAlt,
        title = "Location Photos",
        subtitle = "Show where this item is stored (${state.locationPhotoPaths.size}/3)",
      )

      Text(
        "Capture a photo of the shelf, drawer, or spot where this item lives.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
      )

      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(state.locationPhotoPaths) { index, path ->
          PhotoThumbnail(
            path = path,
            onClick = { viewModel.removePhoto(index, isLocationPhoto = true) },
            badge = "Location",
          )
        }
        if (state.locationPhotoPaths.size < 3) {
          item {
            AddPhotoButton(
              onClick = { locationGalleryLauncher.launch("image/*") },
              label = "Gallery",
            )
          }
        }
      }

      // Location description
      OutlinedTextField(
        value = state.locationDescription ?: "",
        onValueChange = { viewModel.updateLocationDescription(it) },
        label = { Text("Location Description (optional)") },
        placeholder = { Text("e.g., Behind the blue box on the top shelf") },
        minLines = 2, maxLines = 3,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(20.dp)) },
      )

      OutlinedTextField(value = state.notes ?: "", onValueChange = { viewModel.updateNotes(it) },
        label = { Text("Notes (optional)") }, placeholder = { Text("Any additional details...") },
        minLines = 3, maxLines = 5, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))

      Spacer(Modifier.height(4.dp))

      Button(
        onClick = { viewModel.save(); onSaved() },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = state.name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Primary),
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(if (isEditing) "Update Item" else "Save Item", style = MaterialTheme.typography.titleSmall)
      }

      Spacer(Modifier.height(32.dp))
    }

    if (state.isSaving) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IOSColors.Primary)
      }
    }
  }

  // ─── Room Picker Bottom Sheet ───────────────────────────────
  if (showRoomPicker) {
    ModalBottomSheet(
      onDismissRequest = { showRoomPicker = false },
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      containerColor = MaterialTheme.colorScheme.surface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      ) {
        Surface(
          modifier = Modifier.align(Alignment.CenterHorizontally).width(40.dp).height(4.dp),
          shape = RoundedCornerShape(2.dp),
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
        ) {}

        Spacer(Modifier.height(16.dp))
        Text("Move to Room", style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        if (state.isRoomsLoading) {
          Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = IOSColors.Primary)
          }
        } else if (state.availableRooms.isEmpty()) {
          Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.SearchOff, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(40.dp))
              Spacer(Modifier.height(8.dp))
              Text("No other rooms found.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Create a room first.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
          }
        } else {
          val roomsByHome = state.availableRooms.groupBy { it.homeName }
          LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
          ) {
            roomsByHome.forEach { (homeName, roomsInHome) ->
              val roomsByFloor = roomsInHome.groupBy { it.floorName }
              item {
                Text(homeName, style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(vertical = 8.dp))
              }
              roomsByFloor.forEach { (floorName, rooms) ->
                item {
                  Row(
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(Icons.Default.Stairs, contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                      modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(floorName, style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
                rooms.forEach { room ->
                  item {
                    Surface(
                      onClick = {
                        viewModel.moveToRoom(room)
                        showRoomPicker = false
                      },
                      shape = RoundedCornerShape(12.dp),
                      color = if (room.roomId == (state.roomId.ifBlank { roomId }))
                        IOSColors.Primary.copy(alpha = 0.1f)
                      else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, bottom = 4.dp),
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                      ) {
                        Icon(Icons.Default.Room, contentDescription = null,
                          tint = if (room.roomId == (state.roomId.ifBlank { roomId })) IOSColors.Primary
                          else MaterialTheme.colorScheme.onSurfaceVariant,
                          modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(room.roomName, style = MaterialTheme.typography.bodyMedium,
                          fontWeight = if (room.roomId == (state.roomId.ifBlank { roomId })) FontWeight.SemiBold
                          else FontWeight.Normal,
                          modifier = Modifier.weight(1f))
                        if (room.roomId == (state.roomId.ifBlank { roomId })) {
                          Icon(Icons.Default.Check, contentDescription = "Current room",
                            tint = IOSColors.Primary, modifier = Modifier.size(18.dp))
                        }
                      }
                    }
                  }
                }
              }
            }
            item { Spacer(Modifier.height(24.dp)) }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, contentDescription = null, tint = IOSColors.Primary, modifier = Modifier.size(18.dp))
    Spacer(Modifier.width(8.dp))
    Column {
      Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
      Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
private fun PhotoThumbnail(
  path: String,
  onClick: () -> Unit,
  badge: String? = null,
) {
  Box(modifier = Modifier.size(80.dp)) {
    Image(
      painter = rememberAsyncImagePainter(model = File(path)),
      contentDescription = "Photo",
      modifier = Modifier.fillMaxSize()
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
      contentScale = ContentScale.Crop,
    )
    // Delete overlay
    Surface(
      onClick = onClick,
      modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(22.dp),
      shape = RoundedCornerShape(11.dp),
      color = IOSColors.Red.copy(alpha = 0.9f),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(Icons.Default.Close, contentDescription = "Remove",
          tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(14.dp))
      }
    }
    if (badge != null) {
      Surface(
        modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
        shape = RoundedCornerShape(6.dp),
        color = IOSColors.Orange.copy(alpha = 0.9f),
      ) {
        Text(badge, style = MaterialTheme.typography.labelSmall,
          color = androidx.compose.ui.graphics.Color.White,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
      }
    }
  }
}

@Composable
private fun AddPhotoButton(
  onClick: () -> Unit,
  label: String,
) {
  Surface(
    onClick = onClick,
    modifier = Modifier.size(80.dp),
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Icon(Icons.Default.AddAPhoto, contentDescription = null,
        tint = IOSColors.Primary, modifier = Modifier.size(24.dp))
      Spacer(Modifier.height(4.dp))
      Text(label, style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
private fun StatusDot(status: ItemStatus) {
  val color = when (status) {
    ItemStatus.STORED -> IOSColors.Green
    ItemStatus.IN_USE -> IOSColors.Orange
    ItemStatus.LENT_OUT -> IOSColors.Cyan
    ItemStatus.DONATED -> IOSColors.Red
  }
  Surface(modifier = Modifier.size(10.dp), shape = RoundedCornerShape(5.dp), color = color) {}
}
