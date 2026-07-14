package com.stufflocate.app.floorplan

import kotlinx.serialization.Serializable

@Serializable
data class FloorPlan(
  val id: String = "",
  val width: Float = 0f,
  val height: Float = 0f,
  val walls: List<WallSegment> = emptyList(),
  val rooms: List<FloorPlanRoom> = emptyList(),
)

@Serializable
data class WallSegment(
  val id: String = "",
  val startX: Float = 0f,
  val startZ: Float = 0f,
  val endX: Float = 0f,
  val endZ: Float = 0f,
  val height: Float = 2.8f,
)

@Serializable
data class FloorPlanRoom(
  val id: String = "",
  val name: String = "",
  val type: String = "OTHER",
  val polygon: List<Point2D> = emptyList(),
  val furniture: List<FurniturePlacement> = emptyList(),
)

@Serializable
data class Point2D(val x: Float = 0f, val z: Float = 0f)

@Serializable
data class FurniturePlacement(
  val id: String = "",
  val type: String = "",
  val name: String = "",
  val position: Point2D = Point2D(),
  val rotation: Float = 0f,
  val width: Float = 1f,
  val depth: Float = 1f,
  val height: Float = 1f,
  val slots: List<String> = emptyList(),
)

object FurnitureLibrary {
  data class FurnitureType(
    val type: String,
    val displayName: String,
    val emoji: String,
    val defaultWidth: Float,
    val defaultDepth: Float,
    val defaultHeight: Float,
    val slots: List<String>,
  )

  val ALL = listOf(
    FurnitureType("BED", "Bed", "🛏️", 2.0f, 1.5f, 0.5f, listOf("Under Bed")),
    FurnitureType("WARDROBE", "Wardrobe", "👔", 1.2f, 0.6f, 2.0f, listOf("Top Shelf", "Middle Shelf", "Bottom Drawer")),
    FurnitureType("TABLE", "Table", "🪑", 1.2f, 0.8f, 0.75f, emptyList()),
    FurnitureType("DESK", "Desk", "💼", 1.4f, 0.7f, 0.75f, listOf("Top Drawer", "Bottom Drawer")),
    FurnitureType("SHELF", "Shelf", "📚", 1.0f, 0.3f, 2.0f, listOf("Top Shelf", "Middle Shelf", "Bottom Shelf")),
    FurnitureType("SOFA", "Sofa", "🛋️", 2.0f, 0.9f, 0.8f, emptyList()),
    FurnitureType("DRAWER", "Drawer", "🗄️", 0.8f, 0.5f, 0.8f, listOf("Top Drawer", "Bottom Drawer")),
    FurnitureType("CABINET", "Cabinet", "🗃️", 1.0f, 0.5f, 1.2f, listOf("Top Compartment", "Bottom Compartment")),
    FurnitureType("CHAIR", "Chair", "🪑", 0.5f, 0.5f, 0.9f, emptyList()),
    FurnitureType("BOX", "Box", "📦", 0.4f, 0.3f, 0.3f, emptyList()),
  )

  private val byType = ALL.associateBy { it.type }

  fun get(type: String): FurnitureType = byType[type] ?: ALL.last()
}
