package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BatteryMonitor
import com.example.model.BatteryInfo
import com.example.model.HealthStatus
import com.example.model.PluggedState
import com.example.model.PowerProfileType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ahorro de Batería", appName)
  }

  @Test
  fun `verify battery info model defaults`() {
    val info = BatteryInfo(
      level = 80,
      isCharging = false,
      pluggedState = PluggedState.UNPLUGGED,
      health = HealthStatus.GOOD,
      temperatureCelsius = 28.0f,
      voltageMv = 4100
    )
    assertEquals(80, info.level)
    assertEquals("4.10 V", info.formattedVoltage)
    assertEquals("28.0 °C", info.formattedTemperature)
    assertTrue(info.health.isGood)
  }

  @Test
  fun `verify power profile bonus minutes`() {
    val smartProfile = PowerProfileType.SMART
    assertTrue(smartProfile.estimatedBonusMinutes > 0)
    val ultraProfile = PowerProfileType.ULTRA
    assertTrue(ultraProfile.estimatedBonusMinutes > smartProfile.estimatedBonusMinutes)
  }
}
