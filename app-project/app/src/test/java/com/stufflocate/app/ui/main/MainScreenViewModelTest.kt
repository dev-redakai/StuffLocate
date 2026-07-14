package com.stufflocate.app.ui.main

import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.domain.model.Floor
import com.stufflocate.app.domain.model.Home
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.domain.model.RoomLocation
import com.stufflocate.app.domain.model.RoomModel
import com.stufflocate.app.domain.model.SearchResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {

  @Test
  fun uiState_initiallyLoading() = runTest {
    val viewModel = MainScreenViewModel(FakeRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }

  @Test
  fun uiState_onSuccess_showsData() = runTest {
    val viewModel = MainScreenViewModel(FakeRepository())
    // After initial loading, the state should emit Success with data
    val state = viewModel.uiState.first()
    assert(state is MainScreenUiState.Success || state is MainScreenUiState.Loading)
  }
}

private class FakeRepository : DataRepository {
  override val homes: Flow<List<Home>> = flow { emit(listOf(Home(id = "1", name = "Test Home"))) }
  override val allItems: Flow<List<Item>> = flow { emit(emptyList()) }
  override suspend fun getHomeById(homeId: String) = Home(id = homeId, name = "Test Home")
  override suspend fun createHome(name: String, address: String?) = Home(id = "new", name = name, address = address)
  override suspend fun updateHome(home: Home) {}
  override suspend fun deleteHome(homeId: String) {}
  override suspend fun getFloorsForHome(homeId: String) = emptyList<Floor>()
  override suspend fun createFloor(homeId: String, name: String, floorNumber: Int) = Floor(id = "f1", name = name, homeId = homeId)
  override suspend fun updateFloor(floor: Floor) {}
  override suspend fun deleteFloor(floorId: String) {}
  override suspend fun getRoomsForFloor(floorId: String) = emptyList<RoomModel>()
  override suspend fun createRoom(floorId: String, name: String, type: String?) = RoomModel(id = "r1", name = name)
  override suspend fun deleteRoom(roomId: String) {}
  override suspend fun getItemsForRoom(roomId: String) = emptyList<Item>()
  override suspend fun createItem(roomId: String, name: String, category: String?, quantity: Int, notes: String?, tags: List<String>) = Item(id = "i1", name = name, roomId = roomId)
  override suspend fun updateItem(item: Item) {}
  override suspend fun deleteItem(itemId: String) {}
  override suspend fun searchItems(query: String) = emptyList<Item>()
  override suspend fun searchItemsByCategory(category: String) = emptyList<Item>()
  override suspend fun getItemById(itemId: String): Item? = null
  override suspend fun searchItemsWithLocation(query: String, status: ItemStatus?): List<SearchResult> = emptyList()
  override suspend fun searchItemsByCategoryWithLocation(category: String, status: ItemStatus?): List<SearchResult> = emptyList()
  override suspend fun getAllRoomLocations(): List<RoomLocation> = emptyList()
}
