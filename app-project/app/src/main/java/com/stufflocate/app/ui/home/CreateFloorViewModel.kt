package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateFloorUiState(
  val name: String = "",
  val floorNumber: Int = 0,
  val isSaving: Boolean = false,
)

class CreateFloorViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(CreateFloorUiState())
  val uiState: StateFlow<CreateFloorUiState> = _uiState.asStateFlow()

  fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
  fun updateFloorNumber(number: Int) { _uiState.value = _uiState.value.copy(floorNumber = number) }

  fun save(homeId: String) {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        repository.createFloor(homeId, state.name, state.floorNumber)
      } catch (_: Exception) { }
    }
  }
}
