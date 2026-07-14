package com.stufflocate.app.data.local

import com.stufflocate.app.data.local.entity.FloorEntity
import com.stufflocate.app.data.local.entity.HomeEntity
import com.stufflocate.app.data.local.entity.ItemEntity
import com.stufflocate.app.data.local.entity.RoomEntity
import kotlinx.coroutines.flow.first

class DatabaseSeeder(private val homeDao: HomeDao) {
  suspend fun seed() {
    if (homeDao.getAllHomes().first().isNotEmpty()) return

    // ─── Home 1: My Apartment ───
    val home1 = HomeEntity(id = "home-1", name = "My Apartment", address = "123 Main St, Apt 4B")
    homeDao.insertHome(home1)

    val floor1 = FloorEntity(id = "floor-1", homeId = "home-1", name = "Ground Floor", floorNumber = 0, order = 1, type = "MAIN")
    homeDao.insertFloor(floor1)

    val floor2 = FloorEntity(id = "floor-2", homeId = "home-1", name = "First Floor", floorNumber = 1, order = 2, type = "MAIN")
    homeDao.insertFloor(floor2)

    // Ground Floor Rooms
    val livingRoom = RoomEntity(id = "room-lr", floorId = "floor-1", name = "Living Room", type = "LIVING", itemCount = 3)
    homeDao.insertRoom(livingRoom)

    val kitchen = RoomEntity(id = "room-kitchen", floorId = "floor-1", name = "Kitchen", type = "KITCHEN", itemCount = 2)
    homeDao.insertRoom(kitchen)

    val dining = RoomEntity(id = "room-dining", floorId = "floor-1", name = "Dining Room", type = "DINING", itemCount = 1)
    homeDao.insertRoom(dining)

    // First Floor Rooms
    val masterBed = RoomEntity(id = "room-mb", floorId = "floor-2", name = "Master Bedroom", type = "BEDROOM", itemCount = 4)
    homeDao.insertRoom(masterBed)

    val office = RoomEntity(id = "room-office", floorId = "floor-2", name = "Home Office", type = "OFFICE", itemCount = 3)
    homeDao.insertRoom(office)

    // Items in Living Room
    homeDao.insertItem(ItemEntity(id = "item-1", roomId = "room-lr", name = "TV Remote", category = "ELECTRONICS", quantity = 1, status = "STORED", notes = "Samsung TV remote"))
    homeDao.insertItem(ItemEntity(id = "item-2", roomId = "room-lr", name = "Throw Blanket", category = "DECOR", quantity = 2, status = "STORED", tags = "warm,cozy,grey"))
    homeDao.insertItem(ItemEntity(id = "item-3", roomId = "room-lr", name = "Board Games", category = "TOYS", quantity = 5, status = "STORED", tags = "games,family"))

    // Items in Kitchen
    homeDao.insertItem(ItemEntity(id = "item-4", roomId = "room-kitchen", name = "Espresso Machine", category = "KITCHEN", quantity = 1, status = "IN_USE"))
    homeDao.insertItem(ItemEntity(id = "item-5", roomId = "room-kitchen", name = "Chef's Knife Set", category = "KITCHEN", quantity = 1, status = "STORED", notes = "Zwilling, in wooden block"))

    // Items in Master Bedroom
    homeDao.insertItem(ItemEntity(id = "item-6", roomId = "room-mb", name = "Winter Jackets", category = "CLOTHES", quantity = 3, status = "STORED", tags = "winter,warm", locationDescription = "Left side of wardrobe"))
    homeDao.insertItem(ItemEntity(id = "item-7", roomId = "room-mb", name = "Passport", category = "DOCUMENTS", quantity = 1, status = "STORED", notes = "Top drawer of nightstand"))
    homeDao.insertItem(ItemEntity(id = "item-8", roomId = "room-mb", name = "Charging Cables", category = "ELECTRONICS", quantity = 6, status = "STORED", tags = "cables,usb,lightning"))
    homeDao.insertItem(ItemEntity(id = "item-9", roomId = "room-mb", name = "Spare Bedding", category = "CLOTHES", quantity = 4, status = "STORED", locationDescription = "Under-bed storage box"))

    // Items in Home Office
    homeDao.insertItem(ItemEntity(id = "item-10", roomId = "room-office", name = "Monitor Stand", category = "ELECTRONICS", quantity = 1, status = "IN_USE"))
    homeDao.insertItem(ItemEntity(id = "item-11", roomId = "room-office", name = "Printer Paper", category = "OFFICE", quantity = 500, status = "STORED", tags = "paper,printing"))
    homeDao.insertItem(ItemEntity(id = "item-12", roomId = "room-office", name = "Desk Lamp", category = "OFFICE", quantity = 1, status = "IN_USE"))

    // ─── Home 2: Parents' House ───
    val home2 = HomeEntity(id = "home-2", name = "Parents' House", address = "456 Oak Avenue")
    homeDao.insertHome(home2)

    val floor3 = FloorEntity(id = "floor-3", homeId = "home-2", name = "Main Level", floorNumber = 0, order = 1, type = "MAIN")
    homeDao.insertFloor(floor3)

    val garage = RoomEntity(id = "room-garage", floorId = "floor-3", name = "Garage", type = "GARAGE", itemCount = 2)
    homeDao.insertRoom(garage)

    homeDao.insertItem(ItemEntity(id = "item-13", roomId = "room-garage", name = "Power Drill", category = "TOOLS", quantity = 1, status = "STORED", notes = "DeWalt cordless, in red toolbox", tags = "drill,tools,power"))
    homeDao.insertItem(ItemEntity(id = "item-14", roomId = "room-garage", name = "Extension Ladder", category = "TOOLS", quantity = 1, status = "STORED", locationDescription = "Hanging on wall behind door"))

    // Items in Dining Room
    homeDao.insertItem(ItemEntity(id = "item-15", roomId = "room-dining", name = "Wine Collection", category = "KITCHEN", quantity = 24, status = "STORED", notes = "Mixed reds and whites in wine rack"))
  }
}
