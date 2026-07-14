package com.stufflocate.app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data class Main(val dummy: String = "main") : NavKey
@Serializable data class HomeDetail(val homeId: String) : NavKey
@Serializable data class HomeEdit(val homeId: String) : NavKey
@Serializable data class FloorDetail(val floorId: String, val homeId: String) : NavKey
@Serializable data class FloorEdit(val floorId: String, val homeId: String) : NavKey
@Serializable data class RoomDetail(val roomId: String, val roomName: String, val floorId: String) : NavKey
@Serializable data class RoomEdit(val roomId: String, val floorId: String) : NavKey
@Serializable data class CreateHome(val dummy: String = "create") : NavKey
@Serializable data class CreateFloor(val homeId: String) : NavKey
@Serializable data class CreateRoom(val floorId: String, val homeId: String) : NavKey
@Serializable data class CreateItem(val roomId: String, val roomName: String) : NavKey
@Serializable data class EditItem(val itemId: String, val roomId: String) : NavKey
@Serializable data class Search(val dummy: String = "search") : NavKey
@Serializable data class Settings(val dummy: String = "settings") : NavKey
@Serializable data class AllItems(val dummy: String = "all_items") : NavKey
@Serializable data class About(val dummy: String = "about") : NavKey
@Serializable data class FloorPlanEditor(val floorId: String, val homeId: String) : NavKey
@Serializable data class ThreeDView(val floorId: String, val homeId: String) : NavKey
@Serializable data class FurniturePlacementNav(val floorId: String, val homeId: String, val roomId: String) : NavKey
@Serializable data class ThemeGallery(val dummy: String = "theme_gallery") : NavKey
@Serializable data class ThemeEditor(val themeId: String? = null) : NavKey
@Serializable data class ThemeImport(val dummy: String = "theme_import") : NavKey
