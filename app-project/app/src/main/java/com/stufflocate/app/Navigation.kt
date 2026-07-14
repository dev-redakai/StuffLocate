package com.stufflocate.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.ui.home.AllItemsScreen
import com.stufflocate.app.ui.home.CreateFloorScreen
import com.stufflocate.app.ui.home.CreateHomeScreen
import com.stufflocate.app.ui.home.CreateRoomScreen
import com.stufflocate.app.ui.home.FloorEditScreen
import com.stufflocate.app.ui.home.HomeDetailScreen
import com.stufflocate.app.ui.home.HomeEditScreen
import com.stufflocate.app.ui.home.ItemFormScreen
import com.stufflocate.app.ui.home.RoomDetailScreen
import com.stufflocate.app.ui.home.RoomEditScreen
import com.stufflocate.app.ui.home.SearchScreen
import com.stufflocate.app.ui.about.AboutScreen
import com.stufflocate.app.ui.about.ItemDetailScreen
import com.stufflocate.app.ui.main.MainScreen
import com.stufflocate.app.ui.settings.SettingsScreen
import com.stufflocate.app.floorplan.FloorPlan
import com.stufflocate.app.floorplan.FloorPlanEditorScreen
import com.stufflocate.app.floorplan.FurniturePlacementScreen
import com.stufflocate.app.floorplan.ThreeDFloorPlanScreen
import com.stufflocate.app.theme.ThemeEditorScreen
import com.stufflocate.app.theme.ThemeGalleryScreen
import com.stufflocate.app.theme.ThemeImportScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main())

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier,
          )
        }

        entry<HomeDetail> {
          HomeDetailScreen(
            homeId = it.homeId,
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() },
          )
        }

        entry<HomeEdit> {
          HomeEditScreen(
            homeId = it.homeId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<FloorEdit> {
          FloorEditScreen(
            floorId = it.floorId,
            homeId = it.homeId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<RoomDetail> {
          RoomDetailScreen(
            roomId = it.roomId,
            roomName = it.roomName,
            onNavigate = { navKey -> backStack.add(navKey) },
            onBack = { backStack.removeLastOrNull() },
          )
        }

        entry<RoomEdit> {
          RoomEditScreen(
            roomId = it.roomId,
            floorId = it.floorId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<CreateHome> {
          CreateHomeScreen(
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<CreateFloor> {
          CreateFloorScreen(
            homeId = it.homeId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<CreateRoom> {
          CreateRoomScreen(
            floorId = it.floorId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<CreateItem> {
          ItemFormScreen(
            roomId = it.roomId,
            roomName = it.roomName,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<ViewItem> {
          ItemDetailScreen(
            itemId = it.itemId,
            roomId = it.roomId,
            onBack = { backStack.removeLastOrNull() },
            onEdit = { editKey -> backStack.add(editKey) },
          )
        }

        entry<EditItem> {
          ItemFormScreen(
            roomId = it.roomId,
            roomName = "",
            editItemId = it.itemId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<Search> {
          SearchScreen(
            onBack = { backStack.removeLastOrNull() },
            onNavigate = { navKey -> backStack.add(navKey) },
          )
        }

        entry<AllItems> {
          AllItemsScreen(
            onBack = { backStack.removeLastOrNull() },
            onNavigate = { navKey -> backStack.add(navKey) },
          )
        }

        entry<Settings> {
          SettingsScreen(
            onBack = { backStack.removeLastOrNull() },
            onNavigateToThemes = { backStack.add(ThemeGallery()) },
            onNavigateToAbout = { backStack.add(About()) },
          )
        }

        entry<About> {
          AboutScreen(
            onBack = { backStack.removeLastOrNull() },
          )
        }


        entry<FloorPlanEditor> {
          FloorPlanEditorScreen(
            floorId = it.floorId,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
            onView3D = { floorPlan ->
              backStack.add(ThreeDView(it.floorId, it.homeId))
            },
          )
        }

        entry<ThreeDView> {
          ThreeDFloorPlanScreen(
            floorId = it.floorId,
            onBack = { backStack.removeLastOrNull() },
          )
        }

        entry<FurniturePlacementNav> {
          FurniturePlacementScreen(
            floorPlan = FloorPlan(),
            room = com.stufflocate.app.floorplan.FloorPlanRoom(id = it.roomId, name = "Room"),
            onBack = { backStack.removeLastOrNull() },
            onSaved = { backStack.removeLastOrNull() },
          )
        }

        entry<ThemeGallery> {
          val themeManager = ServiceLocator.getAppThemeManager()
          ThemeGalleryScreen(
            themeManager = themeManager,
            onBack = { backStack.removeLastOrNull() },
            onThemeSelected = { backStack.removeLastOrNull() },
            onCreateCustom = { backStack.add(ThemeEditor()) },
            onImportTheme = { backStack.add(ThemeImport()) },
          )
        }

        entry<ThemeEditor> {
          val themeManager = ServiceLocator.getAppThemeManager()
          val existingTheme = it.themeId?.let { id ->
            themeManager.getAllThemes().find { t -> t.id == id }
          }
          ThemeEditorScreen(
            initialTheme = existingTheme,
            onBack = { backStack.removeLastOrNull() },
            onSaved = { theme ->
              themeManager.saveCustomTheme(theme)
              backStack.removeLastOrNull()
            },
          )
        }

        entry<ThemeImport> {
          val themeManager = ServiceLocator.getAppThemeManager()
          ThemeImportScreen(
            themeManager = themeManager,
            onBack = { backStack.removeLastOrNull() },
            onImported = { backStack.removeLastOrNull() },
          )
        }
      },
  )
}
