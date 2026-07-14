package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateHomeUiState(
  val name: String = "",
  val address: String? = null,
  val isSaving: Boolean = false,
)

class CreateHomeViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(CreateHomeUiState())
  val uiState: StateFlow<CreateHomeUiState> = _uiState.asStateFlow()

  fun updateName(name: String) { _uiState.value = _uiState.value.copy(name = name) }
  fun updateAddress(address: String?) { _uiState.value = _uiState.value.copy(address = address) }

  fun save() {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        repository.createHome(state.name, state.address)
      } catch (_: Exception) { }
    }
  }
}
