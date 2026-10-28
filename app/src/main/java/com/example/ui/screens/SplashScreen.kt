package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
  val scale = remember { Animatable(0.8f) }
  val alpha = remember { Animatable(0f) }

  LaunchedEffect(true) {
    scale.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    alpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 600))
    // 3 seconds total splash duration
    delay(2200L)
    onSplashFinished()
  }

  Box(
    modifier =
      Modifier.fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)
          )
        ),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.scale(scale.value).alpha(alpha.value),
    ) {
      Box(
        modifier =
          Modifier.size(110.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Default.TouchApp,
          contentDescription = "App Icon",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(60.dp),
        )
      }
      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = "Touchscreen Test Tool",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Check every touch",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
