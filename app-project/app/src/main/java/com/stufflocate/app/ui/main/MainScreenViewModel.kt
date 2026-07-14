package com.stufflocate.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Home
import com.stufflocate.app.domain.model.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardData(
  val homes: List<Home>,
  val totalItems: Int,
  val recentItems: List<Item>,
)

class MainScreenViewModel(dataRepository: DataRepository) : ViewModel() {
  val uiState: StateFlow<MainScreenUiState> =
    combine(
      dataRepository.homes,
      dataRepository.allItems,
    ) { homes, allItems ->
      val recentItems = allItems.sortedByDescending { it.id }.take(10)
      DashboardData(
        homes = homes,
        totalItems = allItems.size,
        recentItems = recentItems,
      )
    }
      .map<DashboardData, MainScreenUiState> { MainScreenUiState.Success(it) }
      .catch { emit(MainScreenUiState.Error(it)) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainScreenUiState.Loading)
}

sealed interface MainScreenUiState {
  object Loading : MainScreenUiState
  data class Error(val throwable: Throwable) : MainScreenUiState
  data class Success(val data: DashboardData) : MainScreenUiState
}
