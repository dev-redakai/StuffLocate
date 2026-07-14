package com.stufflocate.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
fun FloorEditScreen(
  floorId: String,
  homeId: String,
  onBack: () -> Unit,
  onSaved: () -> Unit,
  viewModel: FloorEditViewModel = viewModel {
    FloorEditViewModel(ServiceLocator.getRepository(), floorId, homeId)
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(state.saved) { if (state.saved) onSaved() }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Edit Floor", fontWeight = FontWeight.Bold) },
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

      RoundIconBox(
        icon = { Icon(Icons.Default.Edit, contentDescription = null,
          tint = IOSColors.Indigo, modifier = Modifier.size(28.dp)) },
        size = 56.dp, color = IOSColors.Indigo,
      )

      OutlinedTextField(
        value = state.name,
        onValueChange = viewModel::updateName,
        label = { Text("Floor Name") },
        placeholder = { Text("e.g., Ground Floor") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
      )

      OutlinedTextField(
        value = state.floorNumber.toString(),
        onValueChange = { viewModel.updateFloorNumber(it.toIntOrNull() ?: 0) },
        label = { Text("Floor Number") },
        placeholder = { Text("0 for ground floor") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        supportingText = { Text("Use 0 for ground floor, 1 for first floor, -1 for basement") },
      )

      Spacer(Modifier.height(4.dp))

      Button(
        onClick = { viewModel.save() },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = state.name.isNotBlank() && !state.isSaving,
        colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Indigo),
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Save Changes", style = MaterialTheme.typography.titleSmall)
      }

      Spacer(Modifier.height(32.dp))
    }

    if (state.isSaving) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IOSColors.Indigo)
      }
    }
  }
}
