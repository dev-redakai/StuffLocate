package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

  private var currentRoomId: String? = null

  fun loadItems(roomId: String) {
    if (roomId == currentRoomId && !_uiState.value.isLoading) return
    currentRoomId = roomId
    _uiState.value = RoomDetailUiState(isLoading = true)

    repository.itemsForRoom(roomId)
      .onEach { items ->
        _uiState.value = RoomDetailUiState(isLoading = false, items = items)
      }
      .launchIn(viewModelScope)
  }

  fun deleteItem(itemId: String, roomId: String) {
    viewModelScope.launch {
      try {
        repository.deleteItem(itemId)
      } catch (_: Exception) { }
    }
  }

  fun updateItemStatus(itemId: String, newStatus: ItemStatus) {
    viewModelScope.launch {
      try {
        val item = repository.getItemById(itemId) ?: return@launch
        repository.updateItem(item.copy(status = newStatus))
      } catch (_: Exception) { }
    }
  }
}
