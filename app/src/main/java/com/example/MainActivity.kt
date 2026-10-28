package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.data.ThemeMode
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CoverageTestScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToTestScreen
import com.example.ui.screens.MultitouchTestScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val userSettings by viewModel.userSettings.collectAsState()

      // Handle keep screen awake
      SideEffect {
        if (userSettings.keepScreenAwake) {
          window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
          window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
      }

      val darkTheme = when (userSettings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
      }
      MyApplicationTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        var showSplash by remember { mutableStateOf(true) }

        if (showSplash) {
          SplashScreen(onSplashFinished = { showSplash = false })
        } else {
          NavHost(navController = navController, startDestination = "home") {
            composable("home") {
              HomeScreen(
                onNavigateToCoverage = { navController.navigate("coverage") },
                onNavigateToMultitouch = { navController.navigate("multitouch") },
                onNavigateToHowTo = { navController.navigate("howto") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAbout = { navController.navigate("about") },
              )
            }
            composable("coverage") {
              CoverageTestScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
              )
            }
            composable("multitouch") {
              MultitouchTestScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
              )
            }
            composable("howto") {
              HowToTestScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable("settings") {
              SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
              )
            }
            composable("about") {
              AboutScreen(onNavigateBack = { navController.popBackStack() })
            }
          }
        }
      }
    }
  }
}
