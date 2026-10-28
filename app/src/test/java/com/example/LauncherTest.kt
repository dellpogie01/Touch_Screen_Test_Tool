package com.example

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [24, 26, 36])
class LauncherTest {
  @Test
  fun `launcher resolves to enabled exported main activity with drawable icons`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
      .addCategory(Intent.CATEGORY_LAUNCHER)
      .setPackage(context.packageName)
    val entries = manager.queryIntentActivities(intent, 0)
    assertEquals(1, entries.size)
    val activity = entries.single().activityInfo
    assertEquals(MainActivity::class.java.name, activity.name)
    assertTrue(activity.enabled)
    assertTrue(activity.exported)
    assertTrue(activity.applicationInfo.enabled)
    assertEquals("TouchTest", activity.loadLabel(manager).toString())
    assertNotNull(context.getDrawable(activity.applicationInfo.icon))
    assertNotNull(context.getDrawable(R.mipmap.ic_launcher_round))
  }
}
