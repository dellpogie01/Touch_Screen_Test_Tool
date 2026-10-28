package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.GridDensity
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverageTestScreen(viewModel: MainViewModel, onNavigateBack: () -> Unit) {
  val userSettings by viewModel.userSettings.collectAsState()
  val touchedCells by viewModel.touchedCells.collectAsState()
  val activeTouches by viewModel.coverageActiveTouches.collectAsState()
  val elapsedTime by viewModel.coverageElapsedTime.collectAsState()

  var showResultsDialog by remember { mutableStateOf(false) }
  var showExitConfirm by remember { mutableStateOf(false) }
  val showGridLines = true

  val density = userSettings.gridDensity
  val totalCells = density.rows * density.cols
  val touchedCount = touchedCells.size
  val coveragePercent = if (totalCells > 0) (touchedCount.toFloat() / totalCells) * 100f else 0f

  DisposableEffect(viewModel, density) {
    viewModel.resetCoverageTest()
    onDispose { viewModel.stopCoverageTest() }
  }

  BackHandler {
    if (touchedCount > 0) {
      showExitConfirm = true
    } else {
      onNavigateBack()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Touch Coverage Test")
            Text(
              text = "Coverage: ${String.format(java.util.Locale.getDefault(), "%.1f", coveragePercent)}% | Time: ${elapsedTime}s",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = {
              if (touchedCount > 0) showExitConfirm = true else onNavigateBack()
            }
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.resetCoverageTest() }) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset")
          }
          IconButton(onClick = { viewModel.stopCoverageTest(); showResultsDialog = true }) {
            Text(
              text = "Finish",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    }
  ) { padding ->
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(padding)
          .pointerInput(density) {
            try {
              awaitPointerEventScope {
                while (true) {
                  val event = awaitPointerEvent()
                  event.changes.forEach { change ->
                    val id = change.id.hashCode()
                    if (change.pressed) {
                      viewModel.registerCoverageTouch(
                        pointerId = id,
                        offset = change.position,
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        density = density,
                      )
                      change.consume()
                    } else {
                      viewModel.removeCoverageTouch(id)
                    }
                  }
                }
              }
            } finally {
              viewModel.stopCoverageTest()
            }
          }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellW = w / density.cols
        val cellH = h / density.rows

        // Draw grid cells
        for (row in 0 until density.rows) {
          for (col in 0 until density.cols) {
            val index = row * density.cols + col
            val isTouched = touchedCells.contains(index)

            val fillColor =
              when {
                isTouched -> Color(0xFF00E676).copy(alpha = 0.45f) // Lime green touched
                else -> Color(0xFF1E273E).copy(alpha = 0.6f) // Untested dark gray
              }

            drawRect(
              color = fillColor,
              topLeft = Offset(col * cellW, row * cellH),
              size = Size(cellW, cellH),
            )

            if (showGridLines) {
              drawRect(
                color = Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(col * cellW, row * cellH),
                size = Size(cellW, cellH),
                style = Stroke(width = 1f),
              )
            }
          }
        }

        // Draw active touches
        activeTouches.values.forEach { offset ->
          drawCircle(color = Color(0xFF00E5FF), radius = 24f, center = offset)
          drawCircle(color = Color.White, radius = 8f, center = offset)
        }
      }

      // Bottom instructions banner
      Surface(
        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
      ) {
        Text(
          text = "Drag across every cell. Gray cells have not registered a touch.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(12.dp),
        )
      }
    }
  }

  // Results Dialog
  if (showResultsDialog) {
    AlertDialog(
      onDismissRequest = { showResultsDialog = false },
      title = { Text("Test Results") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Coverage: ${String.format(java.util.Locale.getDefault(), "%.1f", coveragePercent)}%")
          Text("Touched Cells: $touchedCount / $totalCells")
          Text("Test Duration: ${elapsedTime}s")
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text =
              "Interpretation: Untouched areas may have been missed. " +
                "Repeat the test carefully before drawing conclusions about hardware faults.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showResultsDialog = false
            viewModel.resetCoverageTest()
          }
        ) {
          Text("Retest")
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            showResultsDialog = false
            onNavigateBack()
          }
        ) {
          Text("Return Home")
        }
      },
    )
  }

  // Exit Confirmation Dialog
  if (showExitConfirm) {
    AlertDialog(
      onDismissRequest = { showExitConfirm = false },
      title = { Text("Exit Test?") },
      text = { Text("Active test progress will be lost if you leave now.") },
      confirmButton = {
        Button(
          onClick = {
            showExitConfirm = false
            onNavigateBack()
          }
        ) {
          Text("Exit")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExitConfirm = false }) { Text("Continue Testing") }
      },
    )
  }
}
