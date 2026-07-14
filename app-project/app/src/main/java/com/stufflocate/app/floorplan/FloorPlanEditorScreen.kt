package com.stufflocate.app.floorplan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stufflocate.app.ui.common.IOSColors
import kotlin.math.sqrt

enum class EditorTool {
  WALL, SELECT, PAN, RECT_ROOM, LROOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorPlanEditorScreen(
  onBack: () -> Unit,
  onSaved: (FloorPlan) -> Unit,
  onView3D: (FloorPlan) -> Unit = {},
) {
  var floorPlan by remember { mutableStateOf(FloorPlan(width = 10f, height = 8f)) }
  var activeTool by remember { mutableStateOf(EditorTool.WALL) }
  var wallStart by remember { mutableStateOf<Point2D?>(null) }
  var previewEnd by remember { mutableStateOf<Point2D?>(null) }
  var gridSize by remember { mutableFloatStateOf(0.2f) }
  var showRoomDialog by remember { mutableStateOf(false) }
  var pendingRoomPolygon by remember { mutableStateOf<List<Point2D>>(emptyList()) }
  var pendingRoomName by remember { mutableStateOf("") }
  var pendingRoomType by remember { mutableStateOf("OTHER") }
  var rectStart by remember { mutableStateOf<Point2D?>(null) }
  val textMeasurer = rememberTextMeasurer()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Floor Plan Editor", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          TextButton(onClick = { onView3D(floorPlan) }) {
            Text("3D", color = IOSColors.Secondary, fontWeight = FontWeight.Bold)
          }
          TextButton(onClick = { onSaved(floorPlan) }) {
            Text("Save", color = IOSColors.Primary, fontWeight = FontWeight.Bold)
          }
        },
      )
    },
    bottomBar = {
      FloorPlanToolbar(
        activeTool = activeTool,
        onToolChange = { activeTool = it },
        onUndo = {
          if (floorPlan.walls.isNotEmpty()) {
            floorPlan = floorPlan.copy(walls = floorPlan.walls.dropLast(1))
          }
        },
        onClear = { floorPlan = floorPlan.copy(walls = emptyList(), rooms = emptyList()) },
        wallCount = floorPlan.walls.size,
        roomCount = floorPlan.rooms.size,
      )
    },
  ) { padding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8F9FA)),
    ) {
      FloorPlanCanvas(
        floorPlan = floorPlan,
        activeTool = activeTool,
        wallStart = wallStart,
        previewEnd = previewEnd,
        gridSize = gridSize,
        textMeasurer = textMeasurer,
        onWallStart = { wallStart = it },
        onWallEnd = { end ->
          val start = wallStart ?: return@FloorPlanCanvas
          val snappedEnd = snapToGrid(end, gridSize)
          if (distance(start, snappedEnd) > 0.1f) {
            floorPlan = floorPlan.copy(
              walls = floorPlan.walls + WallSegment(
                id = java.util.UUID.randomUUID().toString(),
                startX = start.x, startZ = start.z,
                endX = snappedEnd.x, endZ = snappedEnd.z,
              )
            )
          }
          wallStart = null
          previewEnd = null
        },
        onPreviewPoint = { previewEnd = it },
        onRectPoint = { point ->
          if (rectStart == null) {
            rectStart = point
          } else {
            val start = rectStart!!
            val minX = minOf(start.x, point.x)
            val maxX = maxOf(start.x, point.x)
            val minZ = minOf(start.z, point.z)
            val maxZ = maxOf(start.z, point.z)
            pendingRoomPolygon = if (activeTool == EditorTool.RECT_ROOM) {
              listOf(Point2D(minX, minZ), Point2D(maxX, minZ), Point2D(maxX, maxZ), Point2D(minX, maxZ))
            } else {
              val midX = (minX + maxX) / 2f
              val midZ = (minZ + maxZ) / 2f
              listOf(
                Point2D(minX, minZ), Point2D(maxX, minZ), Point2D(maxX, midZ),
                Point2D(midX, midZ), Point2D(midX, maxZ), Point2D(minX, maxZ),
              )
            }
            showRoomDialog = true
            rectStart = null
          }
        },
      )

      // Grid size indicator
      Surface(
        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("Grid: ", style = MaterialTheme.typography.labelSmall)
          IconButton(onClick = { gridSize = (gridSize - 0.05f).coerceAtLeast(0.05f) },
            modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Remove, contentDescription = "Smaller grid", modifier = Modifier.size(16.dp))
          }
          Text("${String.format("%.2f", gridSize)}m", style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold)
          IconButton(onClick = { gridSize = (gridSize + 0.05f).coerceAtMost(1f) },
            modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Larger grid", modifier = Modifier.size(16.dp))
          }
        }
      }

      // Dimension display
      Surface(
        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
      ) {
        Text(
          "${floorPlan.walls.size} walls · ${floorPlan.rooms.size} rooms",
          style = MaterialTheme.typography.labelSmall,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
      }
    }
  }

  // Room creation dialog
  if (showRoomDialog) {
    val roomTypes = listOf("BEDROOM", "KITCHEN", "LIVING", "BATHROOM", "OFFICE", "GARAGE", "STORAGE", "OTHER")
    AlertDialog(
      onDismissRequest = { showRoomDialog = false; pendingRoomPolygon = emptyList() },
      containerColor = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(20.dp),
      title = { Text("Create Room", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = pendingRoomName,
            onValueChange = { pendingRoomName = it },
            label = { Text("Room Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
          )
          Text("Room Type", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
          roomTypes.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              row.forEach { type ->
                FilterChip(
                  selected = pendingRoomType == type,
                  onClick = { pendingRoomType = type },
                  label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = {
          if (pendingRoomName.isNotBlank() && pendingRoomPolygon.size >= 3) {
            floorPlan = floorPlan.copy(
              rooms = floorPlan.rooms + FloorPlanRoom(
                id = java.util.UUID.randomUUID().toString(),
                name = pendingRoomName, type = pendingRoomType, polygon = pendingRoomPolygon,
              )
            )
          }
          pendingRoomName = ""; pendingRoomType = "OTHER"; pendingRoomPolygon = emptyList(); showRoomDialog = false
        }) { Text("Create", color = IOSColors.Primary, fontWeight = FontWeight.Bold) }
      },
      dismissButton = {
        TextButton(onClick = { pendingRoomName = ""; pendingRoomType = "OTHER"; pendingRoomPolygon = emptyList(); showRoomDialog = false }) { Text("Cancel") }
      },
    )
  }
}

@Composable
private fun FloorPlanCanvas(
  floorPlan: FloorPlan,
  activeTool: EditorTool,
  wallStart: Point2D?,
  previewEnd: Point2D?,
  gridSize: Float,
  textMeasurer: TextMeasurer,
  onWallStart: (Point2D) -> Unit,
  onWallEnd: (Point2D) -> Unit,
  onPreviewPoint: (Point2D) -> Unit,
  onRectPoint: (Point2D) -> Unit,
) {
  val gridColor = Color(0xFFE0E0E0)
  val wallColor = IOSColors.Primary
  val previewColor = IOSColors.Primary.copy(alpha = 0.5f)

  Canvas(
    modifier = Modifier.fillMaxSize().pointerInput(activeTool) {
      detectTapGestures { offset ->
        val point = screenToFloor(offset, gridSize, size.width.toFloat(), size.height.toFloat())
        val snapped = snapToGrid(point, gridSize)
        when (activeTool) {
          EditorTool.WALL -> {
            if (wallStart == null) onWallStart(snapped) else onWallEnd(snapped)
          }
          EditorTool.RECT_ROOM, EditorTool.LROOM -> onRectPoint(snapped)
          else -> {}
        }
      }
    }.pointerInput(activeTool) {
      if (activeTool == EditorTool.WALL && wallStart != null) {
        detectDragGestures { change, _ ->
          change.consume()
          val point = screenToFloor(change.position, gridSize, size.width.toFloat(), size.height.toFloat())
          onPreviewPoint(snapToGrid(point, gridSize))
        }
      }
    },
  ) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val pixelsPerMeter = (canvasWidth.coerceAtMost(canvasHeight) * 0.8f) / 10f
    val gridSpacing = gridSize * pixelsPerMeter

    // Grid
    var x = 0f
    while (x <= canvasWidth) {
      drawLine(gridColor, Offset(x, 0f), Offset(x, canvasHeight), strokeWidth = 0.5f)
      x += gridSpacing
    }
    var y = 0f
    while (y <= canvasHeight) {
      drawLine(gridColor, Offset(0f, y), Offset(canvasWidth, y), strokeWidth = 0.5f)
      y += gridSpacing
    }

    // Rooms
    val roomColors = listOf(Color(0x225B5FFF), Color(0x2222C55E), Color(0x22F59E0B), Color(0x22EF4444), Color(0x228B5CF6))
    floorPlan.rooms.forEachIndexed { idx, room ->
      if (room.polygon.size >= 3) {
        val path = Path()
        room.polygon.forEachIndexed { i, point ->
          val sx = point.x * pixelsPerMeter + canvasWidth * 0.1f
          val sy = point.z * pixelsPerMeter + canvasHeight * 0.1f
          if (i == 0) path.moveTo(sx, sy) else path.lineTo(sx, sy)
        }
        path.close()
        drawPath(path, roomColors[idx % roomColors.size])
        drawPath(path, wallColor.copy(alpha = 0.3f), style = Stroke(width = 1f))

        val cx = room.polygon.map { it.x }.average().toFloat()
        val cz = room.polygon.map { it.z }.average().toFloat()
        val labelPos = Offset(cx * pixelsPerMeter + canvasWidth * 0.1f, cz * pixelsPerMeter + canvasHeight * 0.1f)
        val style = TextStyle(fontSize = 11.sp, color = IOSColors.Secondary, fontWeight = FontWeight.SemiBold)
        val measured = textMeasurer.measure(room.name, style)
        drawText(measured, topLeft = Offset(labelPos.x - measured.size.width / 2f, labelPos.y - measured.size.height / 2f))
      }
    }

    // Walls
    floorPlan.walls.forEach { wall ->
      val sx = wall.startX * pixelsPerMeter + canvasWidth * 0.1f
      val sz = wall.startZ * pixelsPerMeter + canvasHeight * 0.1f
      val ex = wall.endX * pixelsPerMeter + canvasWidth * 0.1f
      val ez = wall.endZ * pixelsPerMeter + canvasHeight * 0.1f
      drawLine(wallColor, Offset(sx, sz), Offset(ex, ez), strokeWidth = 4f)
      val midX = (sx + ex) / 2f
      val midZ = (sz + ez) / 2f
      val length = distance(Point2D(wall.startX, wall.startZ), Point2D(wall.endX, wall.endZ))
      val label = "${String.format("%.1f", length)}m"
      val style = TextStyle(fontSize = 10.sp, color = IOSColors.Secondary, fontWeight = FontWeight.Medium)
      val measured = textMeasurer.measure(label, style)
      drawText(measured, topLeft = Offset(midX - measured.size.width / 2f, midZ - measured.size.height - 4f))
    }

    // Wall start point
    if (wallStart != null) {
      val sx = wallStart.x * pixelsPerMeter + canvasWidth * 0.1f
      val sz = wallStart.z * pixelsPerMeter + canvasHeight * 0.1f
      drawCircle(wallColor, 6f, Offset(sx, sz))
    }

    // Preview wall
    if (wallStart != null && previewEnd != null) {
      val sx = wallStart.x * pixelsPerMeter + canvasWidth * 0.1f
      val sz = wallStart.z * pixelsPerMeter + canvasHeight * 0.1f
      val ex = previewEnd.x * pixelsPerMeter + canvasWidth * 0.1f
      val ez = previewEnd.z * pixelsPerMeter + canvasHeight * 0.1f
      drawLine(previewColor, Offset(sx, sz), Offset(ex, ez), strokeWidth = 3f)
    }
  }
}

@Composable
private fun FloorPlanToolbar(
  activeTool: EditorTool,
  onToolChange: (EditorTool) -> Unit,
  onUndo: () -> Unit,
  onClear: () -> Unit,
  wallCount: Int,
  roomCount: Int,
) {
  Surface(
    tonalElevation = 3.dp,
    shadowElevation = 8.dp,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      ToolButton(icon = Icons.Default.GridOn, label = "Wall", isActive = activeTool == EditorTool.WALL, onClick = { onToolChange(EditorTool.WALL) })
      ToolButton(icon = Icons.Default.Rectangle, label = "Rect", isActive = activeTool == EditorTool.RECT_ROOM, onClick = { onToolChange(EditorTool.RECT_ROOM) })
      ToolButton(icon = Icons.Default.Layers, label = "L-Shape", isActive = activeTool == EditorTool.LROOM, onClick = { onToolChange(EditorTool.LROOM) })
      ToolButton(icon = Icons.Default.TouchApp, label = "Select", isActive = activeTool == EditorTool.SELECT, onClick = { onToolChange(EditorTool.SELECT) })
      VerticalDivider(modifier = Modifier.height(32.dp))
      IconButton(onClick = onUndo) { Icon(Icons.Default.Undo, contentDescription = "Undo", tint = IOSColors.Secondary) }
      IconButton(onClick = onClear) { Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = IOSColors.Red) }
    }
  }
}

@Composable
private fun ToolButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  isActive: Boolean,
  onClick: () -> Unit,
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    color = if (isActive) IOSColors.Primary.copy(alpha = 0.12f) else Color.Transparent,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(icon, contentDescription = label,
        tint = if (isActive) IOSColors.Primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp))
      Text(label, style = MaterialTheme.typography.labelSmall,
        color = if (isActive) IOSColors.Primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

private fun screenToFloor(screenOffset: Offset, gridSize: Float, canvasWidth: Float, canvasHeight: Float): Point2D {
  val pixelsPerMeter = (canvasWidth.coerceAtMost(canvasHeight) * 0.8f) / 10f
  val x = (screenOffset.x - canvasWidth * 0.1f) / pixelsPerMeter
  val z = (screenOffset.y - canvasHeight * 0.1f) / pixelsPerMeter
  return Point2D(x.coerceAtLeast(0f), z.coerceAtLeast(0f))
}

private fun snapToGrid(point: Point2D, gridSize: Float): Point2D {
  return Point2D(
    (point.x / gridSize).let { kotlin.math.round(it) * gridSize },
    (point.z / gridSize).let { kotlin.math.round(it) * gridSize },
  )
}

private fun distance(a: Point2D, b: Point2D): Float {
  val dx = a.x - b.x
  val dz = a.z - b.z
  return sqrt(dx * dx + dz * dz)
}
