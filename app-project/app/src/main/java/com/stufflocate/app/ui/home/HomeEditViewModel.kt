package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Home
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeEditUiState(
  val name: String = "",
  val address: String? = null,
  val isSaving: Boolean = false,
  val isLoading: Boolean = true,
  val saved: Boolean = false,
)

class HomeEditViewModel(
  private val repository: DataRepository,
  private val homeId: String,
) : ViewModel() {

  private val _uiState = MutableStateFlow(HomeEditUiState())
  val uiState: StateFlow<HomeEditUiState> = _uiState.asStateFlow()

  init {
    loadHome()
  }

  private fun loadHome() {
    viewModelScope.launch {
      try {
        val home = repository.getHomeById(homeId)
        if (home != null) {
          _uiState.value = _uiState.value.copy(
            name = home.name,
            address = home.address,
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
  fun updateAddress(address: String) { _uiState.value = _uiState.value.copy(address = address.ifBlank { null }) }

  fun save() {
    val state = _uiState.value
    if (state.name.isBlank()) return

    viewModelScope.launch {
      _uiState.value = state.copy(isSaving = true)
      try {
        repository.updateHome(
          Home(id = homeId, name = state.name, address = state.address)
        )
        _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isSaving = false)
      }
    }
  }
}
