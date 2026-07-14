package com.stufflocate.app.domain.model

data class Home(
  val id: String,
  val name: String,
  val address: String? = null,
  val floors: List<Floor> = emptyList(),
)

data class Floor(
  val id: String,
  val name: String,
  val homeId: String = "",
  val floorNumber: Int = 0,
  val rooms: List<RoomModel> = emptyList(),
  val type: String? = null,
)

data class RoomModel(
  val id: String,
  val name: String,
  val floorId: String = "",
  val type: String? = null,
  val itemCount: Int = 0,
)

data class Item(
  val id: String,
  val roomId: String = "",
  val name: String,
  val category: String? = null,
  val tags: List<String> = emptyList(),
  val quantity: Int = 1,
  val status: ItemStatus = ItemStatus.STORED,
  val notes: String? = null,
  val imageUrls: List<String> = emptyList(),
  val imagePaths: List<String> = emptyList(),
  val locationPhotoPaths: List<String> = emptyList(),
  val locationDescription: String? = null,
)

data class SearchResult(
  val item: Item,
  val roomName: String,
  val roomType: String?,
  val floorName: String,
  val homeName: String,
  val homeId: String,
  val floorId: String,
  val roomId: String,
  val locationPhotoPaths: List<String> = emptyList(),
)

enum class ItemStatus(val displayName: String) {
  STORED("Stored"),
  IN_USE("In Use"),
  LENT_OUT("Lent Out"),
  DONATED("Donated");

  override fun toString() = displayName
}

object Categories {
  val ALL = listOf(
    "ELECTRONICS", "CLOTHES", "DOCUMENTS", "TOOLS",
    "KITCHEN", "BOOKS", "TOYS", "SPORTS",
    "MEDICINE", "OFFICE", "DECOR", "OTHER"
  )
}

data class RoomLocation(
  val roomId: String,
  val roomName: String,
  val floorName: String,
  val homeName: String,
  val homeId: String,
  val floorId: String,
)
