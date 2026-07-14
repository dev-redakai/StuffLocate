package com.stufflocate.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.ui.common.IOSColors
import com.stufflocate.app.ui.common.RoundIconBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFloorScreen(
  homeId: String,
  onBack: () -> Unit,
  onSaved: () -> Unit,
  viewModel: CreateFloorViewModel = viewModel {
    CreateFloorViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Add Floor", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Spacer(Modifier.height(8.dp))

      RoundIconBox(
        icon = { Icon(Icons.Default.Layers, contentDescription = null, tint = IOSColors.Indigo, modifier = Modifier.size(28.dp)) },
        size = 56.dp, color = IOSColors.Indigo,
      )
      Text("Add a floor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

      OutlinedTextField(
        value = state.name, onValueChange = viewModel::updateName,
        label = { Text("Floor Name") }, placeholder = { Text("e.g., Ground Floor, Basement") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
      )

      OutlinedTextField(
        value = state.floorNumber.toString(), onValueChange = { viewModel.updateFloorNumber(it.toIntOrNull() ?: 0) },
        label = { Text("Floor Number") }, placeholder = { Text("e.g., 0, 1, 2") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        supportingText = { Text("0 = Ground floor, 1 = First floor, -1 = Basement") },
      )

      Spacer(Modifier.weight(1f))

      Button(
        onClick = { viewModel.save(homeId); onSaved() },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = state.name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Indigo),
      ) { Text("Add Floor", style = MaterialTheme.typography.titleSmall) }

      Spacer(Modifier.height(16.dp))
    }
  }
}
