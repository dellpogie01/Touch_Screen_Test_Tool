package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.GridDensity
import com.example.data.ThemeMode
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onNavigateBack: () -> Unit) {
  val userSettings by viewModel.userSettings.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings") },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    }
  ) { padding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(padding)
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Grid Density
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Default Grid Density",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.height(8.dp))
          GridDensity.values().forEach { density ->
            Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = "${density.label} (${density.cols}x${density.rows})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              RadioButton(
                selected = userSettings.gridDensity == density,
                onClick = { viewModel.setGridDensity(density) },
              )
            }
          }
        }
      }

      // Display Preferences
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Display & Touch Preferences",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Show Touch Trails", style = MaterialTheme.typography.bodyMedium)
              Text(
                text = "Render fading trail behind active fingers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Switch(
              checked = userSettings.showTrails,
              onCheckedChange = { viewModel.setShowTrails(it) },
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Show Pointer Coordinates", style = MaterialTheme.typography.bodyMedium)
              Text(
                text = "Display X/Y coordinate badges in multitouch test",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Switch(
              checked = userSettings.showCoordinates,
              onCheckedChange = { viewModel.setShowCoordinates(it) },
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Keep Screen Awake", style = MaterialTheme.typography.bodyMedium)
              Text(
                text = "Prevent screen timeout while testing",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Switch(
              checked = userSettings.keepScreenAwake,
              onCheckedChange = { viewModel.setKeepScreenAwake(it) },
            )
          }
        }
      }
    }
  }
}
