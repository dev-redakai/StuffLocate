package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Home
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeDetailUiState(
  val isLoading: Boolean = false,
  val home: Home? = null,
  val error: Throwable? = null,
  val deleteConfirm: DeleteTarget? = null,
  val homeDeleted: Boolean = false,
)

data class DeleteTarget(val type: String, val id: String, val name: String)

class HomeDetailViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(HomeDetailUiState())
  val uiState: StateFlow<HomeDetailUiState> = _uiState.asStateFlow()

  fun loadHome(homeId: String) {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true)
      try {
        val home = repository.getHomeById(homeId)
        _uiState.value = _uiState.value.copy(isLoading = false, home = home)
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(isLoading = false, error = e)
      }
    }
  }

  fun requestDeleteHome(homeId: String) {
    _uiState.value = _uiState.value.copy(
      deleteConfirm = DeleteTarget("home", homeId, _uiState.value.home?.name ?: "this home")
    )
  }

  fun requestDeleteFloor(floorId: String, floorName: String) {
    _uiState.value = _uiState.value.copy(
      deleteConfirm = DeleteTarget("floor", floorId, floorName)
    )
  }

  fun requestDeleteRoom(roomId: String, roomName: String) {
    _uiState.value = _uiState.value.copy(
      deleteConfirm = DeleteTarget("room", roomId, roomName)
    )
  }

  fun confirmDelete() {
    val target = _uiState.value.deleteConfirm ?: return
    _uiState.value = _uiState.value.copy(deleteConfirm = null)
    viewModelScope.launch {
      try {
        when (target.type) {
          "home" -> {
            repository.deleteHome(target.id)
            _uiState.value = _uiState.value.copy(homeDeleted = true)
          }
          "floor" -> {
            repository.deleteFloor(target.id)
            _uiState.value.home?.let { loadHome(it.id) }
          }
          "room" -> {
            repository.deleteRoom(target.id)
            _uiState.value.home?.let { loadHome(it.id) }
          }
        }
      } catch (_: Exception) { }
    }
  }

  fun cancelDelete() {
    _uiState.value = _uiState.value.copy(deleteConfirm = null)
  }
}
