package com.stufflocate.app.floorplan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stufflocate.app.ui.common.IOSColors
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FurniturePlacementScreen(
  floorPlan: FloorPlan,
  room: FloorPlanRoom,
  onBack: () -> Unit,
  onSaved: (FloorPlanRoom) -> Unit,
) {
  var currentFloorPlan by remember { mutableStateOf(floorPlan) }
  var selectedFurnitureType by remember { mutableStateOf(FurnitureLibrary.ALL.first()) }
  var selectedFurnitureIndex by remember { mutableIntStateOf(-1) }
  val textMeasurer = rememberTextMeasurer()

  val roomIndex = currentFloorPlan.rooms.indexOfFirst { it.id == room.id }
  val currentRoom = currentFloorPlan.rooms.getOrNull(roomIndex) ?: room

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Place Furniture - ${room.name}", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (selectedFurnitureIndex >= 0) {
            IconButton(onClick = {
              val updatedFurniture = currentRoom.furniture.toMutableList()
              updatedFurniture.removeAt(selectedFurnitureIndex)
              val updatedRoom = currentRoom.copy(furniture = updatedFurniture)
              val updatedRooms = currentFloorPlan.rooms.toMutableList()
              updatedRooms[roomIndex] = updatedRoom
              currentFloorPlan = currentFloorPlan.copy(rooms = updatedRooms)
              selectedFurnitureIndex = -1
            }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = IOSColors.Red)
            }
          }
          TextButton(onClick = { onSaved(currentRoom) }) {
            Text("Done", color = IOSColors.Primary, fontWeight = FontWeight.Bold)
          }
        },
      )
    },
    bottomBar = {
      // Furniture type selector
      Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
      ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
          Text(
            "Tap a type, then tap the room to place",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            items(FurnitureLibrary.ALL) { type ->
              FurnitureChip(
                type = type,
                isSelected = selectedFurnitureType.type == type.type,
                onClick = { selectedFurnitureType = type },
              )
            }
          }
        }
      }
    },
  ) { padding ->
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(Color(0xFFF0F2F5))
        .clickable {
          if (selectedFurnitureIndex < 0) {
            val updatedFurniture = currentRoom.furniture.toMutableList() + FurniturePlacement(
              id = java.util.UUID.randomUUID().toString(),
              type = selectedFurnitureType.type,
              name = selectedFurnitureType.displayName,
              position = Point2D(2f, 2f),
              width = selectedFurnitureType.defaultWidth,
              depth = selectedFurnitureType.defaultDepth,
              height = selectedFurnitureType.defaultHeight,
              slots = selectedFurnitureType.slots,
            )
            val updatedRoom = currentRoom.copy(furniture = updatedFurniture)
            val updatedRooms = currentFloorPlan.rooms.toMutableList()
            updatedRooms[roomIndex] = updatedRoom
            currentFloorPlan = currentFloorPlan.copy(rooms = updatedRooms)
            selectedFurnitureIndex = updatedFurniture.lastIndex
          }
        },
    ) {
      val canvasWidth = size.width
      val canvasHeight = size.height
      val scale = minOf(canvasWidth, canvasHeight) / 10f
      val offsetX = canvasWidth * 0.1f
      val offsetZ = canvasHeight * 0.1f

      fun toScreen(x: Float, z: Float) = Offset(offsetX + x * scale, offsetZ + z * scale)

      // Grid
      val gridColor = Color(0xFFDEE2E6)
      for (i in 0..10) {
        val x = offsetX + i * scale
        drawLine(gridColor, Offset(x, offsetZ), Offset(x, offsetZ + 8 * scale), strokeWidth = 0.5f)
        val z = offsetZ + i * scale
        drawLine(gridColor, Offset(offsetX, z), Offset(offsetX + 8 * scale, z), strokeWidth = 0.5f)
      }

      // Room floor
      if (room.polygon.size >= 3) {
        val path = Path()
        room.polygon.forEachIndexed { i, point ->
          val p = toScreen(point.x, point.z)
          if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        path.close()
        drawPath(path, IOSColors.Primary.copy(alpha = 0.08f), style = Fill)
        drawPath(path, IOSColors.Primary.copy(alpha = 0.5f), style = Stroke(width = 2f))
      }

      // Placed furniture
      currentRoom.furniture.forEachIndexed { idx, furn ->
        val color = FurnitureColor(furn.type)
        val isSelected = idx == selectedFurnitureIndex
        val fx = furn.position.x
        val fz = furn.position.z
        val fw = furn.width / 2f
        val fd = furn.depth / 2f

        val corners = listOf(
          toScreen(fx - fw, fz - fd),
          toScreen(fx + fw, fz - fd),
          toScreen(fx + fw, fz + fd),
          toScreen(fx - fw, fz + fd),
        )

        val fillPath = Path().apply {
          moveTo(corners[0].x, corners[0].y)
          corners.drop(1).forEach { lineTo(it.x, it.y) }
          close()
        }
        drawPath(fillPath, color.copy(alpha = if (isSelected) 0.5f else 0.3f), style = Fill)
        drawPath(
          fillPath,
          if (isSelected) IOSColors.Red else IOSColors.Primary,
          style = Stroke(width = if (isSelected) 3f else 1.5f),
        )

        // Label
        val labelPos = toScreen(fx, fz)
        val style = TextStyle(fontSize = 10.sp, color = IOSColors.Primary, fontWeight = FontWeight.SemiBold)
        val measured = textMeasurer.measure(furn.name, style)
        drawText(measured, topLeft = Offset(labelPos.x - measured.size.width / 2f, labelPos.y - measured.size.height / 2f))
      }
    }
  }
}

@Composable
private fun FurnitureChip(
  type: FurnitureLibrary.FurnitureType,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) IOSColors.Primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = Modifier
      .then(if (isSelected) Modifier.border(2.dp, IOSColors.Primary, RoundedCornerShape(12.dp)) else Modifier),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text(type.emoji, fontSize = 16.sp)
      Text(
        type.displayName,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) IOSColors.Primary else MaterialTheme.colorScheme.onSurface,
      )
    }
  }
}

private fun FurnitureColor(type: String) = when (type) {
  "BED" -> Color(0xFF8B5CF6)
  "WARDROBE" -> Color(0xFFF59E0B)
  "TABLE" -> Color(0xFF22C55E)
  "DESK" -> Color(0xFF3B82F6)
  "SHELF" -> Color(0xFFEF4444)
  "SOFA" -> Color(0xFF14B8A6)
  "DRAWER" -> Color(0xFFF97316)
  "CABINET" -> Color(0xFF6366F1)
  "CHAIR" -> Color(0xFFA855F7)
  "BOX" -> Color(0xFF94A3B8)
  else -> Color(0xFF94A3B8)
}
