package com.stufflocate.app.data

import com.stufflocate.app.camera.PhotoManager
import com.stufflocate.app.data.local.HomeDao
import com.stufflocate.app.data.local.entity.FloorEntity
import com.stufflocate.app.data.local.entity.FloorWithRooms
import com.stufflocate.app.data.local.entity.HomeEntity
import com.stufflocate.app.data.local.entity.HomeWithFloors
import com.stufflocate.app.data.local.entity.ItemEntity
import com.stufflocate.app.data.local.entity.ItemSearchResult
import com.stufflocate.app.data.local.entity.RoomEntity
import com.stufflocate.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DefaultDataRepository(
  private val homeDao: HomeDao
) : DataRepository {

  // ───── Helpers ─────

  private fun HomeEntity.toDomain(homeWithFloors: HomeWithFloors? = null): Home {
    val floors = homeWithFloors?.floors?.map { it.toDomain() } ?: emptyList()
    return Home(id = id, name = name, address = address, floors = floors)
  }

  private fun FloorEntity.toDomain(summary: Boolean = false): Floor = Floor(
    id = id,
    name = name,
    homeId = homeId,
    floorNumber = floorNumber,
    type = type,
  )

  private fun FloorWithRooms.toDomain(): Floor = Floor(
    id = floor.id,
    name = floor.name,
    homeId = floor.homeId,
    floorNumber = floor.floorNumber,
    rooms = rooms.map { it.toDomain() },
    type = floor.type,
  )

  private fun RoomEntity.toDomain(): RoomModel = RoomModel(
    id = id,
    name = name,
    floorId = floorId,
    type = type,
    itemCount = itemCount,
  )

  private fun ItemEntity.toDomain(): Item = Item(
    id = id,
    roomId = roomId,
    name = name,
    category = category,
    tags = tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
    quantity = quantity,
    status = try {
      status?.let { ItemStatus.valueOf(it) } ?: ItemStatus.STORED
    } catch (_: IllegalArgumentException) {
      ItemStatus.STORED
    },
    notes = notes,
    locationDescription = locationDescription,
    imagePaths = PhotoManager.parsePhotoPaths(imagePaths),
    locationPhotoPaths = PhotoManager.parsePhotoPaths(locationPhotoPaths),
  )

  // ───── Homes ─────

  override val homes: Flow<List<Home>> =
    homeDao.getAllHomesWithFloors().map { homesWithFloors ->
      homesWithFloors.map { homeWithFloors ->
        // Load floors WITH rooms for accurate counts
        val home = homeWithFloors.home.toDomain(homeWithFloors)
        val floorsWithRooms = homeDao.getFloorsWithRooms(home.id).map { it.toDomain() }
        home.copy(floors = floorsWithRooms)
      }
    }

  override suspend fun getHomeById(homeId: String): Home? {
    // HomeWithFloors only has List<FloorEntity> (without rooms).
    // We need to separately load floors WITH their rooms.
    val homeWithFloors = homeDao.getHomeWithFloors(homeId) ?: return null
    val home = homeWithFloors.home.toDomain(homeWithFloors)
    val floorsWithRooms = homeDao.getFloorsWithRooms(homeId).map { it.toDomain() }
    return home.copy(floors = floorsWithRooms)
  }

  override suspend fun createHome(name: String, address: String?): Home {
    val entity = HomeEntity(id = UUID.randomUUID().toString(), name = name, address = address)
    homeDao.insertHome(entity)
    return entity.toDomain()
  }

  override suspend fun updateHome(home: Home) {
    homeDao.updateHome(HomeEntity(id = home.id, name = home.name, address = home.address))
  }

  override suspend fun deleteHome(homeId: String) {
    homeDao.getHomeById(homeId)?.let { homeDao.deleteHome(it) }
  }

  // ───── Floors ─────

  override suspend fun getFloorsForHome(homeId: String): List<Floor> =
    homeDao.getFloorsWithRooms(homeId).map { it.toDomain() }

  override suspend fun createFloor(homeId: String, name: String, floorNumber: Int): Floor {
    val entity = FloorEntity(
      id = UUID.randomUUID().toString(),
      homeId = homeId,
      name = name,
      floorNumber = floorNumber,
      order = floorNumber,
      type = "MAIN",
    )
    homeDao.insertFloor(entity)
    return Floor(id = entity.id, name = entity.name, homeId = entity.homeId)
  }

  override suspend fun updateFloor(floor: Floor) {
    homeDao.updateFloor(
      FloorEntity(
        id = floor.id,
        homeId = floor.homeId,
        name = floor.name,
        floorNumber = floor.floorNumber,
        order = floor.floorNumber,
        type = floor.type ?: "MAIN",
      )
    )
  }

  override suspend fun deleteFloor(floorId: String) {
    homeDao.deleteFloor(FloorEntity(id = floorId, homeId = "", name = "", floorNumber = 0, order = 0, type = ""))
  }

  override suspend fun getFloorPlan(floorId: String): com.stufflocate.app.floorplan.FloorPlan? {
    val json = homeDao.getFloorById(floorId)?.floorPlanJson ?: return null
    return try {
      kotlinx.serialization.json.Json.decodeFromString<com.stufflocate.app.floorplan.FloorPlan>(json)
    } catch (_: Exception) { null }
  }

  override suspend fun saveFloorPlan(floorId: String, floorPlan: com.stufflocate.app.floorplan.FloorPlan) {
    val json = kotlinx.serialization.json.Json.encodeToString(
      com.stufflocate.app.floorplan.FloorPlan.serializer(), floorPlan
    )
    homeDao.updateFloorPlan(floorId, json)
  }

  // ───── Rooms ─────

  override suspend fun getRoomsForFloor(floorId: String): List<RoomModel> =
    homeDao.getRoomsForFloorSync(floorId).map { it.toDomain() }

  override suspend fun createRoom(floorId: String, name: String, type: String?): RoomModel {
    val entity = RoomEntity(
      id = UUID.randomUUID().toString(),
      floorId = floorId,
      name = name,
      type = type,
      itemCount = 0,
    )
    homeDao.insertRoom(entity)
    return entity.toDomain()
  }

  override suspend fun updateRoom(room: RoomModel) {
    homeDao.getRoomById(room.id)?.let { existing ->
      homeDao.updateRoom(
        existing.copy(name = room.name, type = room.type)
      )
    }
  }

  override suspend fun deleteRoom(roomId: String) {
    homeDao.getRoomById(roomId)?.let { homeDao.deleteRoom(it) }
  }

  // ───── Items ─────

  override val allItems: Flow<List<Item>> =
    homeDao.getAllItems().map { entities -> entities.map { it.toDomain() } }

  override fun itemsForRoom(roomId: String): Flow<List<Item>> =
    homeDao.getItemsForRoom(roomId).map { entities -> entities.map { it.toDomain() } }

  override suspend fun getItemsForRoom(roomId: String): List<Item> =
    homeDao.getItemsForRoomSync(roomId).map { it.toDomain() }

  override suspend fun createItem(
    roomId: String,
    name: String,
    category: String?,
    quantity: Int,
    notes: String?,
    tags: List<String>,
  ): Item {
    val entity = ItemEntity(
      id = UUID.randomUUID().toString(),
      roomId = roomId,
      name = name,
      category = category,
      tags = tags.joinToString(","),
      quantity = quantity,
      notes = notes,
    )
    homeDao.insertItem(entity)
    return entity.toDomain()
  }

  override suspend fun getItemById(itemId: String): Item? =
    homeDao.getItemById(itemId)?.toDomain()

  override suspend fun updateItem(item: Item) {
    homeDao.updateItem(
      ItemEntity(
        id = item.id,
        roomId = item.roomId,
        name = item.name,
        category = item.category,
        tags = item.tags.joinToString(","),
        quantity = item.quantity,
        status = item.status.name,
        notes = item.notes,
        locationDescription = item.locationDescription,
        imagePaths = PhotoManager.toCommaSeparated(item.imagePaths),
        locationPhotoPaths = PhotoManager.toCommaSeparated(item.locationPhotoPaths),
      )
    )
  }

  override suspend fun deleteItem(itemId: String) {
    homeDao.deleteItem(ItemEntity(id = itemId, roomId = "", name = "", quantity = 1))
  }

  // ───── Search ─────

  override suspend fun searchItems(query: String): List<Item> =
    homeDao.searchItems(query).map { it.toDomain() }

  override suspend fun searchItemsByCategory(category: String): List<Item> =
    homeDao.getItemsByCategory(category).map { it.toDomain() }

  // ───── Search with Location Info ─────

  private fun ItemSearchResult.toSearchResult(): SearchResult {
    val item = Item(
      id = itemId,
      roomId = roomId,
      name = itemName,
      category = itemCategory,
      quantity = itemQuantity,
      status = try {
        itemStatus?.let { ItemStatus.valueOf(it) } ?: ItemStatus.STORED
      } catch (_: IllegalArgumentException) {
        ItemStatus.STORED
      },
      notes = itemNotes,
      locationDescription = itemLocationDescription,
      locationPhotoPaths = PhotoManager.parsePhotoPaths(itemLocationPhotoPaths),
    )
    return SearchResult(
      item = item,
      roomName = roomName,
      roomType = roomType,
      floorName = floorName,
      homeName = homeName,
      homeId = homeId,
      floorId = floorId,
      roomId = roomId,
      locationPhotoPaths = PhotoManager.parsePhotoPaths(itemLocationPhotoPaths),
    )
  }

  override suspend fun searchItemsWithLocation(query: String, status: ItemStatus?): List<SearchResult> =
    homeDao.searchItemsWithLocation(query, status?.name).map { it.toSearchResult() }

  override suspend fun searchItemsByCategoryWithLocation(category: String, status: ItemStatus?): List<SearchResult> =
    homeDao.searchItemsByCategoryWithLocation(category, status?.name).map { it.toSearchResult() }

  // ───── Room Locations (for moving items) ─────

  override suspend fun getAllRoomLocations(): List<RoomLocation> =
    homeDao.getAllRoomsWithLocation().map { result ->
      RoomLocation(
        roomId = result.roomId,
        roomName = result.roomName,
        floorName = result.floorName,
        homeName = result.homeName,
        homeId = result.homeId,
        floorId = result.floorId,
      )
    }
}
