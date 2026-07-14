package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.RoomModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RoomEditUiState(
  val name: String = "",
  val type: String? = null,
  val isSaving: Boolean = false,
  val isLoading: Boolean = true,
  val saved: Boolean = false,
)

class RoomEditViewModel(
  private val repository: DataRepository,
  private val roomId: String,
  private val floorId: String,
) : ViewModel() {

  private val _uiState = MutableStateFlow(RoomEditUiState())
  val uiState: StateFlow<RoomEditUiState> = _uiState.asStateFlow()

  init {
    loadRoom()
  }

  private fun loadRoom() {
    viewModelScope.launch {
      try {
        val rooms = repository.getRoomsForFloor(floorId)
        val room = rooms.find { it.id == roomId }
        if (room != null) {
          _uiState.value = _uiState.value.copy(
            name = room.name,
            type = room.type,
            isLoading = false,
          )
        } else {
          _uiState.value = _uiState.value.copy(isLoading = false)
        }
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isLoading = false)
      }
    }
  }

  fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
  fun updateType(type: String?) { _uiState.value = _uiState.value.copy(type = type) }

  fun save() {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        repository.updateRoom(
          RoomModel(id = roomId, floorId = floorId, name = state.name, type = state.type)
        )
        _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isSaving = false)
      }
    }
  }
}
