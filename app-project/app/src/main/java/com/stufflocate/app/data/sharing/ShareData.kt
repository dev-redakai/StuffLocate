package com.stufflocate.app.data.sharing

import com.stufflocate.app.domain.model.Home
import com.stufflocate.app.domain.model.Item
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ShareableItem(
  val name: String,
  val category: String? = null,
  val quantity: Int = 1,
  val notes: String? = null,
  val tags: List<String> = emptyList(),
)

@Serializable
data class ShareableRoom(
  val name: String,
  val type: String? = null,
  val items: List<ShareableItem> = emptyList(),
)

@Serializable
data class ShareableFloor(
  val name: String,
  val floorNumber: Int = 0,
  val rooms: List<ShareableRoom> = emptyList(),
)

@Serializable
data class ShareableHome(
  val name: String,
  val address: String? = null,
  val floors: List<ShareableFloor> = emptyList(),
)

@Serializable
data class SharePayload(
  val version: Int = 1,
  val homes: List<ShareableHome>,
)

object ShareDataMapper {
  private val json = Json { 
    prettyPrint = true
    ignoreUnknownKeys = true
  }

  fun homeToShareable(home: Home, roomItems: Map<String, List<Item>> = emptyMap()): ShareableHome {
    return ShareableHome(
      name = home.name,
      address = home.address,
      floors = home.floors.map { floor ->
        ShareableFloor(
          name = floor.name,
          floorNumber = floor.floorNumber,
          rooms = floor.rooms.map { room ->
            ShareableRoom(
              name = room.name,
              type = room.type,
              items = roomItems[room.id]?.map { item ->
                ShareableItem(
                  name = item.name,
                  category = item.category,
                  quantity = item.quantity,
                  notes = item.notes,
                  tags = item.tags,
                )
              } ?: emptyList(),
            )
          }
        )
      }
    )
  }

  fun homeToJson(home: Home, roomItems: Map<String, List<Item>> = emptyMap()): String {
    val payload = SharePayload(homes = listOf(homeToShareable(home, roomItems)))
    return json.encodeToString(payload)
  }

  fun homesToJson(homes: List<Home>, allRoomItems: Map<String, List<Item>> = emptyMap()): String {
    val payload = SharePayload(homes = homes.map { homeToShareable(it, allRoomItems) })
    return json.encodeToString(payload)
  }

  fun parseJson(data: String): SharePayload? {
    return try {
      json.decodeFromString<SharePayload>(data)
    } catch (_: Exception) {
      null
    }
  }
}
