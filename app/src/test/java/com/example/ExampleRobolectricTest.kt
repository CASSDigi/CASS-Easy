package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.parser.QrContentParser
import com.example.domain.security.QrSecurityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("CASS Easy", appName)
  }

  @Test
  fun `test qr parser wifi format`() {
    val sampleWifi = "WIFI:T:WPA;S:TestNetwork;P:MyPassword;;"
    val parsed = QrContentParser.parse(sampleWifi)
    assertEquals("TestNetwork", parsed.title)
    assertEquals("MyPassword", parsed.details["Password"])
  }

  @Test
  fun `test qr security engine analysis`() {
    val safeUrl = "https://cass-innovations.com"
    val result = QrSecurityEngine.analyze(safeUrl)
    assertNotNull(result)
    assertEquals(true, result.isHttps)
  }
}
