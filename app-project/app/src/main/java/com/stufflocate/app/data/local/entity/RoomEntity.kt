package com.stufflocate.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
  tableName = "rooms",
  foreignKeys = [
    androidx.room.ForeignKey(
      entity = FloorEntity::class,
      parentColumns = ["id"],
      childColumns = ["floorId"],
      onDelete = androidx.room.ForeignKey.CASCADE
    )
  ],
  indices = [androidx.room.Index("floorId")]
)
data class RoomEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val floorId: String,
  val name: String,
  val type: String?,
  val itemCount: Int,
)
