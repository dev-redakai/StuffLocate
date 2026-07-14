package com.stufflocate.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class RoomTypeInfo(
  val key: String,
  val displayName: String,
  val icon: ImageVector,
  val color: Color,
  val emoji: String,
)

object RoomTypeConfig {
  val ALL = listOf(
    RoomTypeInfo("KITCHEN", "Kitchen", Icons.Default.Countertops, Color(0xFF6C63FF), "🍳"),
    RoomTypeInfo("BEDROOM", "Bedroom", Icons.Default.Bed, Color(0xFFFF6584), "🛏️"),
    RoomTypeInfo("LIVING", "Living Room", Icons.Default.Weekend, Color(0xFF45E6FF), "🛋️"),
    RoomTypeInfo("BATHROOM", "Bathroom", Icons.Default.Shower, Color(0xFF34C759), "🚿"),
    RoomTypeInfo("DINING", "Dining Room", Icons.Default.Dining, Color(0xFFFF9F43), "🍽️"),
    RoomTypeInfo("OFFICE", "Office", Icons.Default.Desk, Color(0xFF4A42D4), "💼"),
    RoomTypeInfo("GARAGE", "Garage", Icons.Default.Garage, Color(0xFF8B83FF), "🔧"),
    RoomTypeInfo("STORAGE", "Storage", Icons.Default.Inventory, Color(0xFF2ED573), "📦"),
    RoomTypeInfo("LAUNDRY", "Laundry Room", Icons.Default.LocalLaundryService, Color(0xFF45E6FF), "🧺"),
    RoomTypeInfo("BALCONY", "Balcony", Icons.Default.Deck, Color(0xFFFF9F43), "🌿"),
    RoomTypeInfo("KIDS", "Kids Room", Icons.Default.ChildCare, Color(0xFFFF6584), "🧸"),
    RoomTypeInfo("GUEST", "Guest Room", Icons.Default.Hotel, Color(0xFF8D6E63), "🚪"),
    RoomTypeInfo("GYM", "Gym", Icons.Default.FitnessCenter, Color(0xFFFF3B30), "💪"),
    RoomTypeInfo("LIBRARY", "Library", Icons.Default.MenuBook, Color(0xFF5D4037), "📚"),
    RoomTypeInfo("GAME", "Game Room", Icons.Default.SportsEsports, Color(0xFFC2185B), "🎮"),
    RoomTypeInfo("CELLAR", "Wine Cellar", Icons.Default.WineBar, Color(0xFF6C63FF), "🍷"),
    RoomTypeInfo("ENTRYWAY", "Entryway", Icons.Default.DoorFront, Color(0xFF607D8B), "🚪"),
    RoomTypeInfo("PANTRY", "Pantry", Icons.Default.Kitchen, Color(0xFF2ED573), "🥫"),
    RoomTypeInfo("OTHER", "Other", Icons.Default.MoreHoriz, Color(0xFF757575), "📌"),
  )

  private val byKey = ALL.associateBy { it.key }

  fun get(key: String?): RoomTypeInfo = byKey[key] ?: byKey["OTHER"]!!

  fun getIcon(key: String?): ImageVector = get(key).icon

  fun getColor(key: String?): Color = get(key).color

  fun getEmoji(key: String?): String = get(key).emoji
}
