package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Floor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FloorEditUiState(
  val name: String = "",
  val floorNumber: Int = 0,
  val isSaving: Boolean = false,
  val isLoading: Boolean = true,
  val saved: Boolean = false,
)

class FloorEditViewModel(
  private val repository: DataRepository,
  private val floorId: String,
  private val homeId: String,
) : ViewModel() {

  private val _uiState = MutableStateFlow(FloorEditUiState())
  val uiState: StateFlow<FloorEditUiState> = _uiState.asStateFlow()

  init {
    loadFloor()
  }

  private fun loadFloor() {
    viewModelScope.launch {
      try {
        val floors = repository.getFloorsForHome(homeId)
        val floor = floors.find { it.id == floorId }
        if (floor != null) {
          _uiState.value = _uiState.value.copy(
            name = floor.name,
            floorNumber = floor.floorNumber,
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
  fun updateFloorNumber(number: Int) { _uiState.value = _uiState.value.copy(floorNumber = number) }

  fun save() {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        repository.updateFloor(
          Floor(id = floorId, homeId = homeId, name = state.name, floorNumber = state.floorNumber)
        )
        _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isSaving = false)
      }
    }
  }
}
