package com.stufflocate.app.floorplan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
fun ThreeDFloorPlanScreen(
  floorId: String,
  onBack: () -> Unit,
) {
  var floorPlan by remember { mutableStateOf(FloorPlan()) }
  LaunchedEffect(floorId) {
    val loaded = com.stufflocate.app.di.ServiceLocator.getRepository().getFloorPlan(floorId)
    if (loaded != null) floorPlan = loaded
  }
  var cameraAngle by remember { mutableFloatStateOf(45f) }
  var cameraElevation by remember { mutableFloatStateOf(30f) }
  var zoom by remember { mutableFloatStateOf(1f) }
  val textMeasurer = rememberTextMeasurer()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("3D View", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      // Controls
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Rotation", style = MaterialTheme.typography.labelSmall)
          Slider(
            value = cameraAngle, onValueChange = { cameraAngle = it },
            valueRange = 0f..360f,
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text("Elevation", style = MaterialTheme.typography.labelSmall)
          Slider(
            value = cameraElevation, onValueChange = { cameraElevation = it },
            valueRange = 5f..80f,
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text("Zoom", style = MaterialTheme.typography.labelSmall)
          Slider(
            value = zoom, onValueChange = { zoom = it },
            valueRange = 0.3f..3f,
          )
        }
      }

      // 3D Canvas
      Canvas(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5)),
      ) {
        drawIsometricFloorPlan(
          floorPlan = floorPlan,
          cameraAngle = cameraAngle,
          cameraElevation = cameraElevation,
          zoom = zoom,
          textMeasurer = textMeasurer,
        )
      }
    }
  }
}

private fun DrawScope.drawIsometricFloorPlan(
  floorPlan: FloorPlan,
  cameraAngle: Float,
  cameraElevation: Float,
  zoom: Float,
  textMeasurer: androidx.compose.ui.text.TextMeasurer,
) {
  val canvasWidth = size.width
  val canvasHeight = size.height
  val centerX = canvasWidth / 2f
  val centerZ = canvasHeight / 2f
  val scale = (minOf(canvasWidth, canvasHeight) / 14f) * zoom

  val angleRad = Math.toRadians(cameraAngle.toDouble()).toFloat()
  val elevRad = Math.toRadians(cameraElevation.toDouble()).toFloat()

  val project = fun(x: Float, y: Float, z: Float): Offset {
    val cosA = cos(angleRad)
    val sinA = sin(angleRad)
    val cosE = cos(elevRad)
    val sinE = sin(elevRad)

    val rx = x * cosA - z * sinA
    val rz = x * sinA + z * cosA
    val ry = y

    val px = centerX + rx * scale
    val py = centerZ - rz * scale * cosE + ry * scale * sinE
    return Offset(px, py)
  }

  // Draw floor grid
  val gridColor = Color(0xFFDEE2E6)
  for (i in -5..5) {
    val p1 = project(i.toFloat(), 0f, -5f)
    val p2 = project(i.toFloat(), 0f, 5f)
    drawLine(gridColor, p1, p2, strokeWidth = 0.5f)
    val p3 = project(-5f, 0f, i.toFloat())
    val p4 = project(5f, 0f, i.toFloat())
    drawLine(gridColor, p3, p4, strokeWidth = 0.5f)
  }

  // Draw room floors
  val roomColors = listOf(
    Color(0x225B5FFF), Color(0x2222C55E), Color(0x22F59E0B),
    Color(0x22EF4444), Color(0x228B5CF6),
  )
  floorPlan.rooms.forEachIndexed { idx, room ->
    if (room.polygon.size >= 3) {
      val path = Path()
      room.polygon.forEachIndexed { i, point ->
        val p = project(point.x, 0.01f, point.z)
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
      }
      path.close()
      drawPath(path, roomColors[idx % roomColors.size], style = Fill)
      drawPath(path, IOSColors.Primary.copy(alpha = 0.5f), style = Stroke(width = 1.5f))

      // Room label
      val cx = room.polygon.map { it.x }.average().toFloat()
      val cz = room.polygon.map { it.z }.average().toFloat()
      val labelPos = project(cx, 0.05f, cz)
      val style = TextStyle(fontSize = 11.sp, color = IOSColors.Primary, fontWeight = FontWeight.SemiBold)
      val measured = textMeasurer.measure(room.name, style)
      drawText(measured, topLeft = Offset(labelPos.x - measured.size.width / 2f, labelPos.y - measured.size.height / 2f))
    }
  }

  // Draw walls as 3D boxes
  floorPlan.walls.forEach { wall ->
    drawWall3D(
      wall = wall,
      project = project,
      scale = scale,
    )
  }

  // Draw furniture
  floorPlan.rooms.forEach { room ->
    room.furniture.forEach { furn ->
      drawFurniture3D(furn, project, textMeasurer)
    }
  }

  // Axes indicator
  val axisLen = 0.8f
  val origin = project(-6f, 0f, -6f)
  val xEnd = project(-6f + axisLen, 0f, -6f)
  val yEnd = project(-6f, axisLen, -6f)
  val zEnd = project(-6f, 0f, -6f + axisLen)
  drawLine(Color.Red, origin, xEnd, strokeWidth = 3f)
  drawLine(Color.Green, origin, yEnd, strokeWidth = 3f)
  drawLine(Color.Blue, origin, zEnd, strokeWidth = 3f)
}

private fun DrawScope.drawWall3D(
  wall: WallSegment,
  project: (Float, Float, Float) -> Offset,
  scale: Float,
) {
  val wallHeight = wall.height
  val wallThickness = 0.12f

  val dx = wall.endX - wall.startX
  val dz = wall.endZ - wall.startZ
  val len = Math.sqrt((dx * dx + dz * dz).toDouble()).toFloat()
  if (len < 0.01f) return

  val nx = -dz / len * wallThickness / 2f
  val nz = dx / len * wallThickness / 2f

  // Front face
  val p1 = project(wall.startX + nx, 0f, wall.startZ + nz)
  val p2 = project(wall.endX + nx, 0f, wall.endZ + nz)
  val p3 = project(wall.endX + nx, wallHeight, wall.endZ + nz)
  val p4 = project(wall.startX + nx, wallHeight, wall.startZ + nz)

  val frontPath = Path().apply {
    moveTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    lineTo(p4.x, p4.y)
    close()
  }
  drawPath(frontPath, IOSColors.Primary.copy(alpha = 0.85f), style = Fill)
  drawPath(frontPath, IOSColors.Primary, style = Stroke(width = 1f))

  // Back face
  val p5 = project(wall.startX - nx, 0f, wall.startZ - nz)
  val p6 = project(wall.endX - nx, 0f, wall.endZ - nz)
  val p7 = project(wall.endX - nx, wallHeight, wall.endZ - nz)
  val p8 = project(wall.startX - nx, wallHeight, wall.startZ - nz)

  val backPath = Path().apply {
    moveTo(p5.x, p5.y)
    lineTo(p6.x, p6.y)
    lineTo(p7.x, p7.y)
    lineTo(p8.x, p8.y)
    close()
  }
  drawPath(backPath, IOSColors.Primary.copy(alpha = 0.5f), style = Fill)

  // Top face
  val topPath = Path().apply {
    moveTo(p4.x, p4.y)
    lineTo(p3.x, p3.y)
    lineTo(p7.x, p7.y)
    lineTo(p8.x, p8.y)
    close()
  }
  drawPath(topPath, IOSColors.Primary.copy(alpha = 0.95f), style = Fill)
  drawPath(topPath, IOSColors.Primary, style = Stroke(width = 1f))

  // Side face left
  val sideLeftPath = Path().apply {
    moveTo(p1.x, p1.y)
    lineTo(p5.x, p5.y)
    lineTo(p8.x, p8.y)
    lineTo(p4.x, p4.y)
    close()
  }
  drawPath(sideLeftPath, IOSColors.Primary.copy(alpha = 0.6f), style = Fill)

  // Side face right
  val sideRightPath = Path().apply {
    moveTo(p2.x, p2.y)
    lineTo(p6.x, p6.y)
    lineTo(p7.x, p7.y)
    lineTo(p3.x, p3.y)
    close()
  }
  drawPath(sideRightPath, IOSColors.Primary.copy(alpha = 0.6f), style = Fill)
}

private fun DrawScope.drawFurniture3D(
  furniture: FurniturePlacement,
  project: (Float, Float, Float) -> Offset,
  textMeasurer: androidx.compose.ui.text.TextMeasurer,
) {
  val x = furniture.position.x
  val z = furniture.position.z
  val w = furniture.width / 2f
  val d = furniture.depth / 2f
  val h = furniture.height

  val color = when (furniture.type) {
    "BED" -> Color(0xFF8B5CF6)
    "WARDROBE" -> Color(0xFFF59E0B)
    "TABLE" -> Color(0xFF22C55E)
    "DESK" -> Color(0xFF3B82F6)
    "SHELF" -> Color(0xFFEF4444)
    "SOFA" -> Color(0xFF14B8A6)
    else -> Color(0xFF94A3B8)
  }

  // Top face
  val topCorners = listOf(
    project(x - w, h, z - d),
    project(x + w, h, z - d),
    project(x + w, h, z + d),
    project(x - w, h, z + d),
  )
  val topPath = Path().apply {
    moveTo(topCorners[0].x, topCorners[0].y)
    topCorners.drop(1).forEach { lineTo(it.x, it.y) }
    close()
  }
  drawPath(topPath, color.copy(alpha = 0.9f), style = Fill)
  drawPath(topPath, color, style = Stroke(width = 1f))

  // Front face
  val frontCorners = listOf(
    project(x - w, 0f, z + d),
    project(x + w, 0f, z + d),
    project(x + w, h, z + d),
    project(x - w, h, z + d),
  )
  val frontPath = Path().apply {
    moveTo(frontCorners[0].x, frontCorners[0].y)
    frontCorners.drop(1).forEach { lineTo(it.x, it.y) }
    close()
  }
  drawPath(frontPath, color.copy(alpha = 0.7f), style = Fill)
  drawPath(frontPath, color, style = Stroke(width = 1f))

  // Right face
  val rightCorners = listOf(
    project(x + w, 0f, z - d),
    project(x + w, 0f, z + d),
    project(x + w, h, z + d),
    project(x + w, h, z - d),
  )
  val rightPath = Path().apply {
    moveTo(rightCorners[0].x, rightCorners[0].y)
    rightCorners.drop(1).forEach { lineTo(it.x, it.y) }
    close()
  }
  drawPath(rightPath, color.copy(alpha = 0.5f), style = Fill)

  // Label
  val labelPos = project(x, h + 0.1f, z)
  val style = TextStyle(fontSize = 9.sp, color = IOSColors.Secondary, fontWeight = FontWeight.Medium)
  val measured = textMeasurer.measure(furniture.name, style)
  drawText(measured, topLeft = Offset(labelPos.x - measured.size.width / 2f, labelPos.y - measured.size.height / 2f))
}
