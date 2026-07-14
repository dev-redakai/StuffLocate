package com.stufflocate.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HomeWithFloors(
  @Embedded val home: HomeEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "homeId",
  )
  val floors: List<FloorEntity> = emptyList(),
)

data class FloorWithRooms(
  @Embedded val floor: FloorEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "floorId",
  )
  val rooms: List<RoomEntity> = emptyList(),
)

data class ItemSearchResult(
  val itemId: String,
  val itemName: String,
  val itemCategory: String?,
  val itemQuantity: Int,
  val itemStatus: String?,
  val itemNotes: String?,
  val itemLocationDescription: String?,
  val itemLocationPhotoPaths: String?,
  val roomId: String,
  val roomName: String,
  val roomType: String?,
  val floorId: String,
  val floorName: String,
  val homeId: String,
  val homeName: String,
)

data class RoomLocationResult(
  val roomId: String,
  val roomName: String,
  val roomType: String? = null,
  val floorId: String,
  val floorName: String,
  val homeId: String,
  val homeName: String,
)
