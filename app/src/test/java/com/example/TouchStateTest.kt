package com.example

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.test.core.app.ApplicationProvider
import com.example.data.GridDensity
import com.example.viewmodel.MainViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TouchStateTest {
  private fun model() = MainViewModel(ApplicationProvider.getApplicationContext<Application>())

  @Test fun invalidTouchesDoNotMarkEdgeCells() {
    val model = model()
    for (offset in listOf(Offset(-1f, 10f), Offset(100f, 10f), Offset(Float.NaN, 10f))) {
      model.registerCoverageTouch(1, offset, 100f, 100f, GridDensity.COARSE)
    }
    assertTrue(model.touchedCells.value.isEmpty())
    assertTrue(model.coverageActiveTouches.value.isEmpty())
    assertFalse(model.isCoverageTestRunning.value)
  }

  @Test fun stoppingCoverageClearsActiveTouchesAndRetainsResults() {
    val model = model()
    model.registerCoverageTouch(1, Offset(10f, 10f), 100f, 100f, GridDensity.COARSE)
    model.stopCoverageTest()
    assertEquals(1, model.touchedCells.value.size)
    assertTrue(model.coverageActiveTouches.value.isEmpty())
    assertFalse(model.isCoverageTestRunning.value)
  }

  @Test fun negativePointerIdsHaveValidColors() {
    val model = model()
    model.updatePointer(-1, 10f, 20f)
    assertTrue(model.activePointers.value.getValue(-1).colorIndex in 0..7)
    model.stopMultitouchTest()
    assertTrue(model.activePointers.value.isEmpty())
  }
}
