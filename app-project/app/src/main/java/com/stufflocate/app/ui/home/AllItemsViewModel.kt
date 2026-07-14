package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

data class AllItemsUiState(
  val isLoading: Boolean = true,
  val items: List<Item> = emptyList(),
  val totalQuantity: Int = 0,
)

class AllItemsViewModel(
  private val dataRepository: DataRepository,
) : ViewModel() {
  val uiState: StateFlow<AllItemsUiState> =
    dataRepository.allItems
      .map<List<Item>, AllItemsUiState> { items ->
        AllItemsUiState(
          isLoading = false,
          items = items,
          totalQuantity = items.sumOf { it.quantity },
        )
      }
      .catch { emit(AllItemsUiState(isLoading = false)) }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AllItemsUiState())

  fun updateItemStatus(itemId: String, newStatus: ItemStatus) {
    viewModelScope.launch {
      try {
        val item = dataRepository.getItemById(itemId) ?: return@launch
        dataRepository.updateItem(item.copy(status = newStatus))
        // The allItems Flow will auto-refresh
      } catch (_: Exception) { }
    }
  }

  fun deleteItem(itemId: String) {
    viewModelScope.launch {
      try {
        dataRepository.deleteItem(itemId)
        // The allItems Flow will auto-refresh
      } catch (_: Exception) { }
    }
  }
}
