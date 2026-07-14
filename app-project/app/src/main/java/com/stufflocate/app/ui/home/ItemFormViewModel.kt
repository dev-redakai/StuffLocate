package com.stufflocate.app.ui.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.camera.PhotoManager
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.domain.model.RoomLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemFormUiState(
  val name: String = "",
  val category: String? = null,
  val quantity: Int = 1,
  val status: ItemStatus = ItemStatus.STORED,
  val tagsInput: String = "",
  val tags: List<String> = emptyList(),
  val notes: String? = null,
  val locationDescription: String? = null,
  val roomId: String = "",
  val roomName: String = "",
  val floorName: String = "",
  val homeName: String = "",
  val availableRooms: List<RoomLocation> = emptyList(),
  val isSaving: Boolean = false,
  val isLoading: Boolean = false,
  val isLoaded: Boolean = false,
  val isRoomsLoading: Boolean = false,
  val imagePaths: List<String> = emptyList(),
  val locationPhotoPaths: List<String> = emptyList(),
  val showCameraPreview: Boolean = false,
  val cameraTarget: CameraTarget = CameraTarget.NONE,
)

enum class CameraTarget { NONE, ITEM_PHOTO, LOCATION_PHOTO }

class ItemFormViewModel(
  application: Application,
  private val repository: DataRepository,
  private val roomId: String,
  private val editItemId: String? = null,
) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(ItemFormUiState())
  val uiState: StateFlow<ItemFormUiState> = _uiState.asStateFlow()

  init {
    if (editItemId != null) {
      loadItem(editItemId)
    }
  }

  private fun loadItem(itemId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true)
      try {
        val item = repository.getItemById(itemId)
        if (item != null) {
          _uiState.value = _uiState.value.copy(
            name = item.name,
            category = item.category,
            quantity = item.quantity,
            status = item.status,
            tagsInput = item.tags.joinToString(", "),
            tags = item.tags,
            notes = item.notes,
            locationDescription = item.locationDescription,
            roomId = item.roomId,
            imagePaths = item.imagePaths,
            locationPhotoPaths = item.locationPhotoPaths,
            isLoaded = true,
            isLoading = false,
          )
          resolveRoomNames()
        } else {
          _uiState.value = _uiState.value.copy(isLoading = false)
        }
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isLoading = false)
      }
    }
  }

  private fun resolveRoomNames() {
    viewModelScope.launch {
      try {
        val rooms = repository.getAllRoomLocations()
        val currentRoomId = _uiState.value.roomId.ifBlank { roomId }
        val match = rooms.find { it.roomId == currentRoomId }
        if (match != null) {
          _uiState.value = _uiState.value.copy(
            roomName = match.roomName,
            floorName = match.floorName,
            homeName = match.homeName,
            availableRooms = rooms,
            isRoomsLoading = false,
          )
        } else {
          _uiState.value = _uiState.value.copy(availableRooms = rooms, isRoomsLoading = false)
        }
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isRoomsLoading = false)
      }
    }
  }

  fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
  fun updateCategory(category: String?) { _uiState.value = _uiState.value.copy(category = category) }
  fun updateQuantity(quantity: Int) { _uiState.value = _uiState.value.copy(quantity = quantity.coerceAtLeast(1)) }
  fun updateStatus(status: ItemStatus) { _uiState.value = _uiState.value.copy(status = status) }

  fun updateTagsInput(input: String) {
    val tags = input.split(",").map { it.trim() }.filter { it.isNotBlank() }
    _uiState.value = _uiState.value.copy(tagsInput = input, tags = tags)
  }

  fun updateNotes(notes: String) { _uiState.value = _uiState.value.copy(notes = notes.ifBlank { null }) }
  fun updateLocationDescription(desc: String) { _uiState.value = _uiState.value.copy(locationDescription = desc.ifBlank { null }) }

  fun moveToRoom(roomLocation: RoomLocation) {
    _uiState.value = _uiState.value.copy(
      roomId = roomLocation.roomId,
      roomName = roomLocation.roomName,
      floorName = roomLocation.floorName,
      homeName = roomLocation.homeName,
    )
  }

  fun addPhoto(uri: Uri, isLocationPhoto: Boolean) {
    val context = getApplication<Application>()
    val dirName = if (isLocationPhoto) "location_photos" else "item_photos"
    val path = PhotoManager.savePhoto(context, uri, dirName) ?: return

    if (isLocationPhoto) {
      val current = _uiState.value.locationPhotoPaths
      if (current.size < 3) {
        _uiState.value = _uiState.value.copy(locationPhotoPaths = current + path)
      }
    } else {
      val current = _uiState.value.imagePaths
      if (current.size < 5) {
        _uiState.value = _uiState.value.copy(imagePaths = current + path)
      }
    }
  }

  fun removePhoto(index: Int, isLocationPhoto: Boolean) {
    val context = getApplication<Application>()
    if (isLocationPhoto) {
      val paths = _uiState.value.locationPhotoPaths.toMutableList()
      if (index in paths.indices) {
        PhotoManager.deletePhoto(context, paths.removeAt(index))
        _uiState.value = _uiState.value.copy(locationPhotoPaths = paths)
      }
    } else {
      val paths = _uiState.value.imagePaths.toMutableList()
      if (index in paths.indices) {
        PhotoManager.deletePhoto(context, paths.removeAt(index))
        _uiState.value = _uiState.value.copy(imagePaths = paths)
      }
    }
  }

  fun openCamera(target: CameraTarget) {
    _uiState.value = _uiState.value.copy(showCameraPreview = true, cameraTarget = target)
  }

  fun closeCamera() {
    _uiState.value = _uiState.value.copy(showCameraPreview = false, cameraTarget = CameraTarget.NONE)
  }

  fun onPhotoCaptured(bitmap: android.graphics.Bitmap) {
    val context = getApplication<Application>()
    val target = _uiState.value.cameraTarget
    val dirName = when (target) {
      CameraTarget.ITEM_PHOTO -> "item_photos"
      CameraTarget.LOCATION_PHOTO -> "location_photos"
      CameraTarget.NONE -> return
    }
    val path = PhotoManager.savePhotoFromBitmap(context, bitmap, dirName) ?: return

    if (target == CameraTarget.LOCATION_PHOTO) {
      val current = _uiState.value.locationPhotoPaths
      if (current.size < 3) {
        _uiState.value = _uiState.value.copy(locationPhotoPaths = current + path)
      }
    } else {
      val current = _uiState.value.imagePaths
      if (current.size < 5) {
        _uiState.value = _uiState.value.copy(imagePaths = current + path)
      }
    }
    closeCamera()
  }

  fun save() {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        if (editItemId != null) {
          repository.updateItem(
            Item(
              id = editItemId,
              roomId = state.roomId.ifBlank { roomId },
              name = state.name,
              category = state.category,
              tags = state.tags,
              quantity = state.quantity,
              status = state.status,
              notes = state.notes,
              locationDescription = state.locationDescription,
              imagePaths = state.imagePaths,
              locationPhotoPaths = state.locationPhotoPaths,
            )
          )
        } else {
          val item = repository.createItem(
            roomId = roomId,
            name = state.name,
            category = state.category,
            quantity = state.quantity,
            notes = state.notes,
            tags = state.tags,
          )
          // Update with photos after creation
          if (state.imagePaths.isNotEmpty() || state.locationPhotoPaths.isNotEmpty()) {
            repository.updateItem(
              item.copy(
                imagePaths = state.imagePaths,
                locationPhotoPaths = state.locationPhotoPaths,
                locationDescription = state.locationDescription,
              )
            )
          }
        }
        _uiState.value = _uiState.value.copy(isSaving = false)
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isSaving = false)
      }
    }
  }
}
