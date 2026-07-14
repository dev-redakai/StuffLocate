package com.stufflocate.app.data.local

import androidx.room.*
import com.stufflocate.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeDao {
  // ───── Homes with Relations ─────
  @Transaction
  @Query("SELECT * FROM homes")
  fun getAllHomesWithFloors(): Flow<List<HomeWithFloors>>

  @Transaction
  @Query("SELECT * FROM homes WHERE id = :homeId")
  suspend fun getHomeWithFloors(homeId: String): HomeWithFloors?

  @Transaction
  @Query("SELECT * FROM floors WHERE homeId = :homeId ORDER BY floorNumber ASC")
  suspend fun getFloorsWithRooms(homeId: String): List<FloorWithRooms>

  // ───── Homes ─────
  @Query("SELECT * FROM homes")
  fun getAllHomes(): Flow<List<HomeEntity>>

  @Query("SELECT * FROM homes WHERE id = :homeId")
  suspend fun getHomeById(homeId: String): HomeEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHome(home: HomeEntity)

  @Update
  suspend fun updateHome(home: HomeEntity)

  @Delete
  suspend fun deleteHome(home: HomeEntity)

  // ───── Floors ─────
  @Query("SELECT * FROM floors WHERE homeId = :homeId ORDER BY floorNumber ASC")
  suspend fun getFloorsForHomeSync(homeId: String): List<FloorEntity>

  @Query("SELECT * FROM floors WHERE homeId = :homeId")
  fun getFloorsForHome(homeId: String): Flow<List<FloorEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFloor(floor: FloorEntity)

  @Update
  suspend fun updateFloor(floor: FloorEntity)

  @Delete
  suspend fun deleteFloor(floor: FloorEntity)

  // ───── Rooms ─────
  @Query("SELECT * FROM rooms WHERE floorId = :floorId ORDER BY name ASC")
  suspend fun getRoomsForFloorSync(floorId: String): List<RoomEntity>

  @Query("SELECT * FROM rooms WHERE floorId = :floorId")
  fun getRoomsForFloor(floorId: String): Flow<List<RoomEntity>>

  @Query("SELECT * FROM rooms WHERE id = :roomId")
  suspend fun getRoomById(roomId: String): RoomEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRoom(room: RoomEntity)

  @Update
  suspend fun updateRoom(room: RoomEntity)

  @Delete
  suspend fun deleteRoom(room: RoomEntity)

  // ───── Locations ─────
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRoomLocation(roomLocation: LocationEntity)

  // ───── Items ─────
  @Query("SELECT * FROM items ORDER BY name ASC")
  fun getAllItems(): Flow<List<ItemEntity>>

  @Query("SELECT * FROM items WHERE roomId = :roomId ORDER BY name ASC")
  suspend fun getItemsForRoomSync(roomId: String): List<ItemEntity>

  @Query("SELECT * FROM items WHERE id = :itemId")
  suspend fun getItemById(itemId: String): ItemEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: ItemEntity)

  @Update
  suspend fun updateItem(item: ItemEntity)

  @Delete
  suspend fun deleteItem(item: ItemEntity)

  // ───── Search ─────
  @Query("""
    SELECT * FROM items 
    WHERE name LIKE '%' || :query || '%' 
       OR category LIKE '%' || :query || '%' 
       OR notes LIKE '%' || :query || '%' 
       OR tags LIKE '%' || :query || '%' 
       OR locationDescription LIKE '%' || :query || '%'
    ORDER BY name ASC
  """)
  suspend fun searchItems(query: String): List<ItemEntity>

  @Query("SELECT * FROM items WHERE category = :category ORDER BY name ASC")
  suspend fun getItemsByCategory(category: String): List<ItemEntity>

  // ───── Search with Location Info ─────
  @Query("""
    SELECT 
      i.id AS itemId,
      i.name AS itemName,
      i.category AS itemCategory,
      i.quantity AS itemQuantity,
      i.status AS itemStatus,
      i.notes AS itemNotes,
      i.locationDescription AS itemLocationDescription,
      i.locationPhotoPaths AS itemLocationPhotoPaths,
      r.id AS roomId,
      r.name AS roomName,
      r.type AS roomType,
      f.id AS floorId,
      f.name AS floorName,
      h.id AS homeId,
      h.name AS homeName
    FROM items i
    JOIN rooms r ON i.roomId = r.id
    JOIN floors f ON r.floorId = f.id
    JOIN homes h ON f.homeId = h.id
    WHERE (i.name LIKE '%' || :query || '%' 
       OR i.category LIKE '%' || :query || '%' 
       OR i.notes LIKE '%' || :query || '%' 
       OR i.tags LIKE '%' || :query || '%' 
       OR i.locationDescription LIKE '%' || :query || '%')
      AND (:status IS NULL OR i.status = :status)
    ORDER BY i.name ASC
  """)
  suspend fun searchItemsWithLocation(query: String, status: String? = null): List<ItemSearchResult>

  @Query("""
    SELECT 
      i.id AS itemId,
      i.name AS itemName,
      i.category AS itemCategory,
      i.quantity AS itemQuantity,
      i.status AS itemStatus,
      i.notes AS itemNotes,
      i.locationDescription AS itemLocationDescription,
      i.locationPhotoPaths AS itemLocationPhotoPaths,
      r.id AS roomId,
      r.name AS roomName,
      r.type AS roomType,
      f.id AS floorId,
      f.name AS floorName,
      h.id AS homeId,
      h.name AS homeName
    FROM items i
    JOIN rooms r ON i.roomId = r.id
    JOIN floors f ON r.floorId = f.id
    JOIN homes h ON f.homeId = h.id
    WHERE i.category = :category
      AND (:status IS NULL OR i.status = :status)
    ORDER BY i.name ASC
  """)
  suspend fun searchItemsByCategoryWithLocation(category: String, status: String? = null): List<ItemSearchResult>

  // ───── All Rooms with Location Info (for item moves) ─────
  @Query("""
    SELECT r.id AS roomId, r.name AS roomName, '' AS roomType,
           f.id AS floorId, f.name AS floorName,
           h.id AS homeId, h.name AS homeName
    FROM rooms r
    JOIN floors f ON r.floorId = f.id
    JOIN homes h ON f.homeId = h.id
    ORDER BY h.name ASC, f.floorNumber ASC, r.name ASC
  """)
  suspend fun getAllRoomsWithLocation(): List<RoomLocationResult>
}
