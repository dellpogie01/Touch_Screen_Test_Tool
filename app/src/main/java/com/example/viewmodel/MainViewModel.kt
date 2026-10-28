package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GridDensity
import com.example.data.SettingsManager
import com.example.data.ThemeMode
import com.example.data.UserSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TouchPoint(val x: Float, val y: Float, val timestamp: Long = System.currentTimeMillis())

data class PointerInfo(
  val id: Int,
  val x: Float,
  val y: Float,
  val colorIndex: Int,
  val trail: List<Offset> = emptyList(),
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val settingsManager = SettingsManager(application)

  private val _userSettings = MutableStateFlow(UserSettings())
  val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

  // Coverage Test State
  private val _touchedCells = MutableStateFlow<Set<Int>>(emptySet())
  val touchedCells: StateFlow<Set<Int>> = _touchedCells.asStateFlow()

  private val _coverageActiveTouches = MutableStateFlow<Map<Int, Offset>>(emptyMap())
  val coverageActiveTouches: StateFlow<Map<Int, Offset>> = _coverageActiveTouches.asStateFlow()

  private val _coverageElapsedTime = MutableStateFlow(0L)
  val coverageElapsedTime: StateFlow<Long> = _coverageElapsedTime.asStateFlow()

  private val _isCoverageTestRunning = MutableStateFlow(false)
  val isCoverageTestRunning: StateFlow<Boolean> = _isCoverageTestRunning.asStateFlow()

  // Multitouch Test State
  private val _activePointers = MutableStateFlow<Map<Int, PointerInfo>>(emptyMap())
  val activePointers: StateFlow<Map<Int, PointerInfo>> = _activePointers.asStateFlow()

  private val _peakSimultaneousTouches = MutableStateFlow(0)
  val peakSimultaneousTouches: StateFlow<Int> = _peakSimultaneousTouches.asStateFlow()

  private val _multitouchElapsedTime = MutableStateFlow(0L)
  val multitouchElapsedTime: StateFlow<Long> = _multitouchElapsedTime.asStateFlow()

  private val _isMultitouchTestRunning = MutableStateFlow(false)
  val isMultitouchTestRunning: StateFlow<Boolean> = _isMultitouchTestRunning.asStateFlow()

  private var coverageTimerJob: Job? = null
  private var multitouchTimerJob: Job? = null

  init {
    viewModelScope.launch {
      settingsManager.settingsFlow.collect { settings -> _userSettings.value = settings }
    }
  }

  // Settings actions
  fun setThemeMode(mode: ThemeMode) {
    viewModelScope.launch { settingsManager.setThemeMode(mode) }
  }

  fun setGridDensity(density: GridDensity) {
    viewModelScope.launch { settingsManager.setGridDensity(density) }
  }

  fun setShowTrails(show: Boolean) {
    viewModelScope.launch { settingsManager.setShowTrails(show) }
  }

  fun setShowCoordinates(show: Boolean) {
    viewModelScope.launch { settingsManager.setShowCoordinates(show) }
  }

  fun setKeepScreenAwake(awake: Boolean) {
    viewModelScope.launch { settingsManager.setKeepScreenAwake(awake) }
  }

  // Coverage Test control
  fun startCoverageTest() {
    if (!_isCoverageTestRunning.value) {
      _isCoverageTestRunning.value = true
      startCoverageTimer()
    }
  }

  fun resetCoverageTest() {
    stopCoverageTest()
    _touchedCells.value = emptySet()
    _coverageActiveTouches.value = emptyMap()
    _coverageElapsedTime.value = 0L
    startCoverageTest()
  }

  fun stopCoverageTest() {
    _isCoverageTestRunning.value = false
    _coverageActiveTouches.value = emptyMap()
    coverageTimerJob?.cancel()
  }

  private fun startCoverageTimer() {
    coverageTimerJob?.cancel()
    coverageTimerJob =
      viewModelScope.launch {
        while (_isCoverageTestRunning.value) {
          delay(1000L)
          _coverageElapsedTime.update { it + 1L }
        }
      }
  }

  fun registerCoverageTouch(pointerId: Int, offset: Offset, width: Float, height: Float, density: GridDensity) {
    if (!width.isFinite() || !height.isFinite() || width <= 0f || height <= 0f ||
      !offset.x.isFinite() || !offset.y.isFinite() ||
      offset.x < 0f || offset.y < 0f || offset.x >= width || offset.y >= height) {
      removeCoverageTouch(pointerId)
      return
    }
    if (!_isCoverageTestRunning.value) startCoverageTest()

    _coverageActiveTouches.update { current ->
      current.toMutableMap().apply { put(pointerId, offset) }
    }

    // Calculate grid cell index
    val cellWidth = width / density.cols
    val cellHeight = height / density.rows

    if (cellWidth > 0f && cellHeight > 0f) {
      val col = (offset.x / cellWidth).toInt().coerceIn(0, density.cols - 1)
      val row = (offset.y / cellHeight).toInt().coerceIn(0, density.rows - 1)
      val cellIndex = row * density.cols + col

      if (!_touchedCells.value.contains(cellIndex)) {
        _touchedCells.update { it + cellIndex }
      }
    }
  }

  fun removeCoverageTouch(pointerId: Int) {
    _coverageActiveTouches.update { current ->
      current.toMutableMap().apply { remove(pointerId) }
    }
  }

  // Multitouch Test control
  fun startMultitouchTest() {
    if (!_isMultitouchTestRunning.value) {
      _isMultitouchTestRunning.value = true
      startMultitouchTimer()
    }
  }

  fun resetMultitouchPeak() {
    _peakSimultaneousTouches.value = _activePointers.value.size
  }

  fun resetMultitouchTest() {
    stopMultitouchTest()
    _activePointers.value = emptyMap()
    _peakSimultaneousTouches.value = 0
    _multitouchElapsedTime.value = 0L
    startMultitouchTest()
  }

  fun stopMultitouchTest() {
    _isMultitouchTestRunning.value = false
    _activePointers.value = emptyMap()
    multitouchTimerJob?.cancel()
  }

  private fun startMultitouchTimer() {
    multitouchTimerJob?.cancel()
    multitouchTimerJob =
      viewModelScope.launch {
        while (_isMultitouchTestRunning.value) {
          delay(1000L)
          _multitouchElapsedTime.update { it + 1L }
        }
      }
  }

  fun updatePointer(id: Int, x: Float, y: Float) {
    if (!_isMultitouchTestRunning.value) startMultitouchTest()

    _activePointers.update { current ->
      val mutable = current.toMutableMap()
      val existing = mutable[id]
      val colorIndex = existing?.colorIndex ?: Math.floorMod(id, 8)
      val newTrail = (existing?.trail.orEmpty() + Offset(x, y)).takeLast(15)
      mutable[id] = PointerInfo(id = id, x = x, y = y, colorIndex = colorIndex, trail = newTrail)

      val peak = _peakSimultaneousTouches.value
      if (mutable.size > peak) {
        _peakSimultaneousTouches.value = mutable.size
      }

      mutable
    }
  }

  fun removePointer(id: Int) {
    _activePointers.update { current ->
      val mutable = current.toMutableMap()
      mutable.remove(id)
      mutable
    }
  }

  fun clearAllPointers() {
    _activePointers.value = emptyMap()
  }

  override fun onCleared() {
    super.onCleared()
    coverageTimerJob?.cancel()
    multitouchTimerJob?.cancel()
  }
}
