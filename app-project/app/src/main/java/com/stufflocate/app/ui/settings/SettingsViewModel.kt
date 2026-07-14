package com.stufflocate.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.data.sharing.ShareDataMapper
import com.stufflocate.app.domain.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
  val exportedData: String = "",
  val isExporting: Boolean = false,
  val importMessage: String? = null,
)

class SettingsViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

  fun exportAllHomes() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isExporting = true)
      try {
        val homes = repository.homes.first()
        // Load items for each room to include in export
        val allRoomItems = mutableMapOf<String, List<Item>>()
        for (home in homes) {
          for (floor in home.floors) {
            for (room in floor.rooms) {
              allRoomItems[room.id] = repository.getItemsForRoom(room.id)
            }
          }
        }
        val json = ShareDataMapper.homesToJson(homes, allRoomItems)
        _uiState.value = _uiState.value.copy(exportedData = json, isExporting = false)
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isExporting = false)
      }
    }
  }

  fun importHomes(jsonData: String) {
    viewModelScope.launch {
      try {
        val payload = ShareDataMapper.parseJson(jsonData)
        if (payload != null) {
          var totalItems = 0
          for (home in payload.homes) {
            val createdHome = repository.createHome(home.name, home.address)
            for (floor in home.floors) {
              val createdFloor = repository.createFloor(createdHome.id, floor.name, floor.floorNumber)
              for (room in floor.rooms) {
                val createdRoom = repository.createRoom(createdFloor.id, room.name, room.type)
                for (item in room.items) {
                  repository.createItem(
                    roomId = createdRoom.id,
                    name = item.name,
                    category = item.category,
                    quantity = item.quantity,
                    notes = item.notes,
                    tags = item.tags,
                  )
                  totalItems++
                }
              }
            }
          }
          _uiState.value = _uiState.value.copy(
            importMessage = "✅ Imported ${payload.homes.size} home(s) with $totalItems item(s) successfully!"
          )
        } else {
          _uiState.value = _uiState.value.copy(importMessage = "❌ Invalid data format. Please check the JSON.")
        }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(importMessage = "❌ Import failed: ${e.message}")
      }
    }
  }

  fun clearImportMessage() {
    _uiState.value = _uiState.value.copy(importMessage = null)
  }
}
