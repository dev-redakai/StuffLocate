package com.stufflocate.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
  tableName = "locations",
  foreignKeys = [
    androidx.room.ForeignKey(
      entity = RoomEntity::class,
      parentColumns = ["id"],
      childColumns = ["roomId"],
      onDelete = androidx.room.ForeignKey.CASCADE
    )
  ],
  indices = [androidx.room.Index("roomId")]
)
data class LocationEntity(
  @PrimaryKey val id: String,
  val roomId: String,
  val name: String,
  val order: Int
)

