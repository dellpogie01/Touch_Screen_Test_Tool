package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.viewmodel.MainViewModel

val PointerColors =
  listOf(
    Color(0xFF00E5FF), // Cyan
    Color(0xFF00E676), // Lime
    Color(0xFFFFEA00), // Yellow
    Color(0xFFFF9100), // Orange
    Color(0xFFFF1744), // Red
    Color(0xFFD500F9), // Purple
    Color(0xFF651FFF), // Deep Purple
    Color(0xFF00B0FF), // Light Blue
  )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultitouchTestScreen(viewModel: MainViewModel, onNavigateBack: () -> Unit) {
  val activePointers by viewModel.activePointers.collectAsState()
  val peakTouches by viewModel.peakSimultaneousTouches.collectAsState()
  val elapsedTime by viewModel.multitouchElapsedTime.collectAsState()
  val userSettings by viewModel.userSettings.collectAsState()

  DisposableEffect(viewModel) {
    viewModel.resetMultitouchTest()
    onDispose { viewModel.stopMultitouchTest() }
  }

  BackHandler {
    viewModel.stopMultitouchTest()
    onNavigateBack()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Multitouch Test")
            Text(
              text =
                "Active: ${activePointers.size} | Peak: $peakTouches | Time: ${elapsedTime}s",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = {
              viewModel.stopMultitouchTest()
              onNavigateBack()
            }
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { viewModel.resetMultitouchPeak() }) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset Peak")
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
          .pointerInput(Unit) {
            try {
              awaitPointerEventScope {
                while (true) {
                  val event = awaitPointerEvent()
                  viewModel.startMultitouchTest()

                  val currentChanges = event.changes

                  currentChanges.forEach { change ->
                    val id = change.id.hashCode()
                    if (change.pressed) {
                      viewModel.updatePointer(id, change.position.x, change.position.y)
                      change.consume()
                    } else {
                      viewModel.removePointer(id)
                    }
                  }
                }
              }
            } finally {
              viewModel.clearAllPointers()
            }
          }
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        activePointers.values.forEach { pointer ->
          val color = PointerColors[pointer.colorIndex % PointerColors.size]

          // Draw touch trail
          if (userSettings.showTrails && pointer.trail.size > 1) {
            for (i in 0 until pointer.trail.size - 1) {
              val p1 = pointer.trail[i]
              val p2 = pointer.trail[i + 1]
              val alpha = (i + 1).toFloat() / pointer.trail.size
              drawLine(color = color.copy(alpha = alpha * 0.5f), start = p1, end = p2, strokeWidth = 6f)
            }
          }

          // Draw outer ripple ring
          drawCircle(color = color.copy(alpha = 0.3f), radius = 70f, center = Offset(pointer.x, pointer.y))
          // Draw inner circle
          drawCircle(color = color, radius = 40f, center = Offset(pointer.x, pointer.y))
          drawCircle(
            color = Color.Black,
            radius = 40f,
            center = Offset(pointer.x, pointer.y),
            style = Stroke(width = 2f),
          )

          // Draw coordinates
          if (userSettings.showCoordinates) {
            // Text rendering in canvas can be done or handled via overlays
          }
        }
      }

      // Overlay coordinate badges for active pointers
      if (userSettings.showCoordinates) {
        Column(
          modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          activePointers.values.forEach { pointer ->
            val color = PointerColors[pointer.colorIndex % PointerColors.size]
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            ) {
              Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Box(
                  modifier =
                    Modifier.background(color, RoundedCornerShape(4.dp))
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "ID ${pointer.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "X: ${pointer.x.toInt()}  Y: ${pointer.y.toInt()}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }
        }
      }

      // Bottom guidance
      Surface(
        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
      ) {
        Text(
          text = "Place multiple fingers on screen to test simultaneous touch limits.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.padding(12.dp),
        )
      }
    }
  }
}
