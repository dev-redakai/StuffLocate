package com.stufflocate.app.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.ui.common.IOSColors
import com.stufflocate.app.ui.common.RoundIconBox
import com.stufflocate.app.ui.common.RoomTypeConfig

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRoomScreen(
  floorId: String,
  onBack: () -> Unit,
  onSaved: () -> Unit,
  viewModel: CreateRoomViewModel = viewModel {
    CreateRoomViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Add Room", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Spacer(Modifier.height(8.dp))

      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        RoundIconBox(
          icon = { Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = IOSColors.Primary, modifier = Modifier.size(28.dp)) },
          size = 56.dp, color = IOSColors.Primary,
        )
        Spacer(Modifier.height(12.dp))
        Text("Add a new room", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Choose a type and give it a name",
          style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }

      OutlinedTextField(
        value = state.name, onValueChange = viewModel::updateName,
        label = { Text("Room Name") }, placeholder = { Text("e.g., Master Bedroom, Kitchen") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
      )

      Text("Select Room Type", style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold, color = IOSColors.Primary, modifier = Modifier.padding(top = 4.dp))

      // Room type flow grid
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        RoomTypeConfig.ALL.forEach { roomType ->
          val isSelected = state.type == roomType.key
          Surface(
            onClick = { viewModel.updateType(roomType.key) },
            modifier = Modifier
              .widthIn(min = 100.dp)
              .clip(RoundedCornerShape(14.dp))
              .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) roomType.color else Color.Transparent,
                shape = RoundedCornerShape(14.dp),
              ),
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) roomType.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(roomType.emoji, style = MaterialTheme.typography.titleMedium)
              Spacer(Modifier.width(6.dp))
              Text(roomType.displayName, style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) roomType.color else MaterialTheme.colorScheme.onSurfaceVariant)
              if (isSelected) {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Check, contentDescription = null, tint = roomType.color, modifier = Modifier.size(14.dp))
              }
            }
          }
        }
      }

      Spacer(Modifier.weight(1f))

      Button(
        onClick = { viewModel.save(floorId); onSaved() },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = state.name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Primary),
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Add Room", style = MaterialTheme.typography.titleSmall)
      }

      Spacer(Modifier.height(16.dp))
    }
  }
}
