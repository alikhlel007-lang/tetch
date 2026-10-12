package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.OrderStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertNotNull(appName)
  }

  @Test
  fun `verify order statuses`() {
    assertEquals(OrderStatus.NEW, OrderStatus.fromCode("new"))
    assertEquals(OrderStatus.PREPARING, OrderStatus.fromCode("preparing"))
    assertEquals(OrderStatus.DELIVERED, OrderStatus.fromCode("delivered"))
    assertEquals(OrderStatus.BILLED, OrderStatus.fromCode("billed"))
    assertEquals(OrderStatus.PAID, OrderStatus.fromCode("paid"))
  }
}
