package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToTestScreen(onNavigateBack: () -> Unit) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("How to Test") },
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
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Step-by-Step Testing Guide",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(12.dp))

          StepItem(number = "1", title = "Clean and dry your screen", description = "Remove dust, smudges, and moisture which can interfere with touch registration.")
          StepItem(number = "2", title = "Start Touch Coverage Test", description = "Select grid density and slowly drag your finger across every cell on the screen.")
          StepItem(number = "3", title = "Inspect untouched cells", description = "If any cell remains gray, repeat the swipe carefully. Untouched cells may simply have been missed.")
          StepItem(number = "4", title = "Run Multitouch Test", description = "Place multiple fingers simultaneously on the screen to observe active contact points and peak count.")
        }
      }

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Diagnostic Limits & Important Notes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• Screen protectors, heavy cases, moisture, and gloves can dampen or block touch sensitivity.\n\n" +
              "• System gestures, navigation bars, and display cutouts may limit edge testing.\n\n" +
              "• This app cannot calibrate, repair, or definitively diagnose touchscreen hardware faults. No software test can observe touch events the operating system never reports.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
fun StepItem(number: String, title: String, description: String) {
  Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier.size(32.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = number,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
