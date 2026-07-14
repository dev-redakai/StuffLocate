package com.stufflocate.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.domain.model.SearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
  val query: String = "",
  val selectedCategory: String? = null,
  val selectedStatus: ItemStatus? = null,
  val results: List<SearchResult> = emptyList(),
  val isSearching: Boolean = false,
)

class SearchViewModel(
  private val repository: DataRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(SearchUiState())
  val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

  private var searchJob: Job? = null

  fun updateQuery(query: String) {
    _uiState.value = _uiState.value.copy(query = query)
    performSearch()
  }

  fun toggleCategory(category: String?) {
    val newCategory = if (_uiState.value.selectedCategory == category) null else category
    _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
    performSearch()
  }

  fun toggleStatus(status: ItemStatus?) {
    val newStatus = if (_uiState.value.selectedStatus == status) null else status
    _uiState.value = _uiState.value.copy(selectedStatus = newStatus)
    performSearch()
  }

  private fun performSearch() {
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      val state = _uiState.value

      // Debounce text queries
      if (state.query.isNotBlank()) {
        delay(300)
      }

      _uiState.value = state.copy(isSearching = true)

      try {
        val results = when {
          // Both text and category → use text search (most flexible)
          state.query.isNotBlank() ->
            repository.searchItemsWithLocation(state.query, state.selectedStatus)
          state.selectedCategory != null ->
            repository.searchItemsByCategoryWithLocation(state.selectedCategory, state.selectedStatus)
          // Only status filter, no text or category
          state.selectedStatus != null ->
            repository.searchItemsWithLocation("", state.selectedStatus)
          else -> emptyList()
        }
        _uiState.value = _uiState.value.copy(results = results, isSearching = false)
      } catch (_: Exception) {
        _uiState.value = _uiState.value.copy(isSearching = false)
      }
    }
  }

  fun updateItemStatus(itemId: String, newStatus: ItemStatus) {
    viewModelScope.launch {
      try {
        val item = repository.getItemById(itemId) ?: return@launch
        repository.updateItem(item.copy(status = newStatus))
        // Update local state so UI reflects immediately
        val updated = _uiState.value.results.map { result ->
          if (result.item.id == itemId) {
            result.copy(item = result.item.copy(status = newStatus))
          } else result
        }
        _uiState.value = _uiState.value.copy(results = updated)
      } catch (_: Exception) { }
    }
  }

  fun deleteItem(itemId: String) {
    viewModelScope.launch {
      try {
        repository.deleteItem(itemId)
        // Re-run the current search to refresh results
        performSearch()
      } catch (_: Exception) { }
    }
  }
}
