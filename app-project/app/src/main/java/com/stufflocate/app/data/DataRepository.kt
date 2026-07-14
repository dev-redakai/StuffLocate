package com.stufflocate.app.data

import com.stufflocate.app.domain.model.Floor
import com.stufflocate.app.domain.model.Home
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.domain.model.RoomModel
import com.stufflocate.app.domain.model.ItemStatus
import com.stufflocate.app.domain.model.RoomLocation
import com.stufflocate.app.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface DataRepository {
  // Homes
  val homes: Flow<List<Home>>
  suspend fun getHomeById(homeId: String): Home?
  suspend fun createHome(name: String, address: String? = null): Home
  suspend fun updateHome(home: Home)
  suspend fun deleteHome(homeId: String)

  // Floors
  suspend fun getFloorsForHome(homeId: String): List<Floor>
  suspend fun createFloor(homeId: String, name: String, floorNumber: Int): Floor
  suspend fun updateFloor(floor: Floor)
  suspend fun deleteFloor(floorId: String)
  suspend fun getFloorPlan(floorId: String): com.stufflocate.app.floorplan.FloorPlan?
  suspend fun saveFloorPlan(floorId: String, floorPlan: com.stufflocate.app.floorplan.FloorPlan)

  // Rooms
  suspend fun getRoomsForFloor(floorId: String): List<RoomModel>
  suspend fun createRoom(floorId: String, name: String, type: String?): RoomModel
  suspend fun updateRoom(room: RoomModel)
  suspend fun deleteRoom(roomId: String)

  // Items
  val allItems: Flow<List<Item>>
  fun itemsForRoom(roomId: String): Flow<List<Item>>
  suspend fun getItemsForRoom(roomId: String): List<Item>
  suspend fun createItem(
    roomId: String,
    name: String,
    category: String?,
    quantity: Int,
    notes: String?,
    tags: List<String> = emptyList(),
  ): Item
  suspend fun getItemById(itemId: String): Item?
  suspend fun updateItem(item: Item)
  suspend fun deleteItem(itemId: String)

  // Search
  suspend fun searchItems(query: String): List<Item>
  suspend fun searchItemsByCategory(category: String): List<Item>

  // Search with Location Info
  suspend fun searchItemsWithLocation(query: String, status: ItemStatus? = null): List<SearchResult>
  suspend fun searchItemsByCategoryWithLocation(category: String, status: ItemStatus? = null): List<SearchResult>

  // Room Locations (for moving items)
  suspend fun getAllRoomLocations(): List<RoomLocation>
}
