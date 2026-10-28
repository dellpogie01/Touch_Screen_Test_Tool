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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onNavigateToCoverage: () -> Unit,
  onNavigateToMultitouch: () -> Unit,
  onNavigateToHowTo: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToAbout: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Touchscreen Test Tool") },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        actions = {
          IconButton(onClick = onNavigateToSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
          }
          IconButton(onClick = onNavigateToAbout) {
            Icon(Icons.Default.Info, contentDescription = "About")
          }
        },
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
      // Intro Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier =
                Modifier.size(48.dp)
                  .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
              )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                text = "Screen Health Diagnostics",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                text = "Inspect touch coverage & multitouch limits",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text =
              "Check where your screen registers touch and how many fingers it detects at once. " +
                "Fully offline with zero permissions required.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      // Main Test Actions
      Text(
        text = "Diagnostic Tests",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp),
      )

      MenuCard(
        title = "Touch Coverage Test",
        description = "Drag across every cell to verify touch response across the entire screen grid.",
        icon = Icons.Default.GridView,
        onClick = onNavigateToCoverage,
      )

      MenuCard(
        title = "Multitouch Test",
        description = "Test simultaneous multi-finger contact points and track session peak count.",
        icon = Icons.Default.TouchApp,
        onClick = onNavigateToMultitouch,
      )

      // Secondary Actions
      Text(
        text = "Resources & Configuration",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp).padding(top = 8.dp),
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SecondaryActionButton(
          title = "How to Test",
          icon = Icons.AutoMirrored.Filled.Help,
          onClick = onNavigateToHowTo,
          modifier = Modifier.weight(1f),
        )
        SecondaryActionButton(
          title = "Settings",
          icon = Icons.Default.Settings,
          onClick = onNavigateToSettings,
          modifier = Modifier.weight(1f),
        )
      }

      // Hardware Limitation Notice Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Hardware Limitation Notice",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "This app shows touch events received by Android. It cannot repair your screen or confirm a hardware fault.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
fun MenuCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(52.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp),
          )
        }
      }
      Spacer(modifier = Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
fun SecondaryActionButton(
  title: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  OutlinedButton(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    modifier = modifier.height(56.dp),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
  ) {
    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = title, style = MaterialTheme.typography.bodyMedium)
  }
}
