package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class GridDensity(val rows: Int, val cols: Int, val label: String) {
  COARSE(12, 8, "Coarse"),
  MEDIUM(20, 12, "Medium"),
  FINE(32, 18, "Fine")
}

enum class ThemeMode(val label: String) {
  SYSTEM("System"),
  DARK("Dark"),
  LIGHT("Light")
}

data class UserSettings(
  val themeMode: ThemeMode = ThemeMode.DARK,
  val gridDensity: GridDensity = GridDensity.MEDIUM,
  val showTrails: Boolean = true,
  val showCoordinates: Boolean = true,
  val keepScreenAwake: Boolean = true,
)

class SettingsManager(context: Context) {
  private val dataStore = context.applicationContext.dataStore

  companion object {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val GRID_DENSITY_KEY = stringPreferencesKey("grid_density")
    private val SHOW_TRAILS_KEY = booleanPreferencesKey("show_trails")
    private val SHOW_COORDS_KEY = booleanPreferencesKey("show_coordinates")
    private val KEEP_AWAKE_KEY = booleanPreferencesKey("keep_screen_awake")
  }

  val settingsFlow: Flow<UserSettings> =
    dataStore.data.map { prefs ->
      val themeStr = prefs[THEME_KEY] ?: ThemeMode.DARK.name
      val densityStr = prefs[GRID_DENSITY_KEY] ?: GridDensity.MEDIUM.name

      val themeMode =
        try {
          ThemeMode.valueOf(themeStr)
        } catch (e: Exception) {
          ThemeMode.DARK
        }
      val gridDensity =
        try {
          GridDensity.valueOf(densityStr)
        } catch (e: Exception) {
          GridDensity.MEDIUM
        }

      UserSettings(
        themeMode = themeMode,
        gridDensity = gridDensity,
        showTrails = prefs[SHOW_TRAILS_KEY] ?: true,
        showCoordinates = prefs[SHOW_COORDS_KEY] ?: true,
        keepScreenAwake = prefs[KEEP_AWAKE_KEY] ?: true,
      )
    }

  suspend fun setThemeMode(mode: ThemeMode) {
    dataStore.edit { it[THEME_KEY] = mode.name }
  }

  suspend fun setGridDensity(density: GridDensity) {
    dataStore.edit { it[GRID_DENSITY_KEY] = density.name }
  }

  suspend fun setShowTrails(show: Boolean) {
    dataStore.edit { it[SHOW_TRAILS_KEY] = show }
  }

  suspend fun setShowCoordinates(show: Boolean) {
    dataStore.edit { it[SHOW_COORDS_KEY] = show }
  }

  suspend fun setKeepScreenAwake(awake: Boolean) {
    dataStore.edit { it[KEEP_AWAKE_KEY] = awake }
  }
}
