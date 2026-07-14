package com.stufflocate.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
fun CreateHomeScreen(
  onBack: () -> Unit,
  onSaved: () -> Unit,
  viewModel: CreateHomeViewModel = viewModel {
    CreateHomeViewModel(ServiceLocator.getRepository())
  },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Create Home", fontWeight = FontWeight.Bold) },
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
        icon = { Icon(Icons.Default.Home, contentDescription = null, tint = IOSColors.Primary, modifier = Modifier.size(28.dp)) },
        size = 56.dp, color = IOSColors.Primary,
      )
      Text("Give your home a name", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

      OutlinedTextField(
        value = state.name, onValueChange = viewModel::updateName,
        label = { Text("Home Name") }, placeholder = { Text("e.g., My Apartment, Office") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
      )

      OutlinedTextField(
        value = state.address ?: "", onValueChange = { viewModel.updateAddress(it.ifBlank { null }) },
        label = { Text("Address (optional)") }, placeholder = { Text("e.g., 123 Main St") },
        singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
      )

      Spacer(Modifier.weight(1f))

      Button(
        onClick = { viewModel.save(); onSaved() },
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        enabled = state.name.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = IOSColors.Primary),
      ) { Text("Create Home", style = MaterialTheme.typography.titleSmall) }

      Spacer(Modifier.height(16.dp))
    }

    if (state.isSaving) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IOSColors.Primary)
      }
    }
  }
}
