package com.stufflocate.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
  tableName = "items",
  foreignKeys = [
    androidx.room.ForeignKey(
      entity = RoomEntity::class,
      parentColumns = ["id"],
      childColumns = ["roomId"],
      onDelete = androidx.room.ForeignKey.CASCADE
    ),
    androidx.room.ForeignKey(
      entity = LocationEntity::class,
      parentColumns = ["id"],
      childColumns = ["locationId"],
      onDelete = androidx.room.ForeignKey.CASCADE
    )
  ],
  indices = [androidx.room.Index("roomId"), androidx.room.Index("locationId")]
)
data class ItemEntity(
  @PrimaryKey val id: String,
  val roomId: String = "",
  val locationId: String? = null,
  val name: String,
  val category: String? = null,
  val tags: String? = null,
  val quantity: Int = 1,
  val status: String? = "STORED",
  val notes: String? = null,
  val locationDescription: String? = null,
  val imagePaths: String? = null,
  val locationPhotoPaths: String? = null,
)
