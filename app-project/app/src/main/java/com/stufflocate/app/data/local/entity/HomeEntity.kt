package com.stufflocate.app.data.local.entity

import androidx.room.*
import java.util.UUID

@Entity(tableName = "homes")
data class HomeEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val name: String,
  val address: String? = null,
)
