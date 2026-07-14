package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoomDetailUiState(
  val isLoading: Boolean = true,
  val items: List<Item> = emptyList(),
  val error: Throwable? = null,
)

class RoomDetailViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(RoomDetailUiState())
  val uiState: StateFlow<RoomDetailUiState> = _uiState.asStateFlow()

  fun loadItems(roomId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true)
      try {
        val items = repository.getItemsForRoom(roomId)
        _uiState.value = RoomDetailUiState(isLoading = false, items = items)
      } catch (e: Exception) {
        _uiState.value = RoomDetailUiState(isLoading = false, error = e)
      }
    }
  }

  fun deleteItem(itemId: String, roomId: String) {
    viewModelScope.launch {
      try {
        repository.deleteItem(itemId)
        loadItems(roomId)
      } catch (_: Exception) { }
    }
  }

  fun updateItemStatus(itemId: String, newStatus: ItemStatus) {
    viewModelScope.launch {
      try {
        val item = repository.getItemById(itemId) ?: return@launch
        repository.updateItem(item.copy(status = newStatus))
        // Refresh the list to show updated status
        val currentRoomId = _uiState.value.items.firstOrNull()?.roomId ?: return@launch
        loadItems(currentRoomId)
      } catch (_: Exception) { }
    }
  }
}
