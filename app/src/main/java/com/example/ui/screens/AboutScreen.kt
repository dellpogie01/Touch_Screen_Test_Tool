package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("About") },
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
            text = "Touchscreen Test Tool",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text =
              "A high-precision, offline-first Android utility designed to inspect touch coverage, " +
                "verify multitouch performance, and identify unresponsive display regions.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Privacy & Permissions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "This application requires zero special permissions and does not access the internet, " +
              "camera, location, or storage. All tests execute entirely on-device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }
  }
}
