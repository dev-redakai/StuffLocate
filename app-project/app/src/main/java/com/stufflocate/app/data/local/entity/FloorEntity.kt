package com.stufflocate.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
  tableName = "floors",
  foreignKeys = [
    androidx.room.ForeignKey(
      entity = HomeEntity::class,
      parentColumns = ["id"],
      childColumns = ["homeId"],
      onDelete = androidx.room.ForeignKey.CASCADE
    )
  ],
  indices = [androidx.room.Index("homeId")]
)
data class FloorEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val homeId: String,
  val name: String,
  val floorNumber: Int,
  val order: Int,
  val type: String,
)
