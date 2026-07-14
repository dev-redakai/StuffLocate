package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateRoomUiState(
  val name: String = "",
  val type: String? = null,
  val isSaving: Boolean = false,
)

class CreateRoomViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(CreateRoomUiState())
  val uiState: StateFlow<CreateRoomUiState> = _uiState.asStateFlow()

  fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
  fun updateType(type: String) { _uiState.value = _uiState.value.copy(type = type) }

  fun save(floorId: String) {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        repository.createRoom(floorId, state.name, state.type)
      } catch (_: Exception) { }
    }
  }
}
