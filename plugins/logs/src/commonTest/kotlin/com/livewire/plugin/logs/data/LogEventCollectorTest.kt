package com.livewire.plugin.logs.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LogEventCollectorTest {

  @Test
  fun recordsEventsNewestFirst() {
    LogEventCollector.clear()

    LogEventCollector.log(LogLevel.Debug, "tag", "first")
    LogEventCollector.log(LogLevel.Info, "tag", "second")

    val events = LogEventCollector.events.value
    assertEquals(2, events.size)
    assertEquals("second", events[0].message)
    assertEquals("first", events[1].message)
  }

  @Test
  fun capturesLevelTagAndStackTrace() {
    LogEventCollector.clear()

    LogEventCollector.log(LogLevel.Error, "auth", "login failed", IllegalStateException("boom"))

    val event = LogEventCollector.events.value.single()
    assertEquals(LogLevel.Error, event.level)
    assertEquals("auth", event.tag)
    assertEquals("login failed", event.message)
    assertNotNull(event.stackTrace)
    assertTrue(event.stackTrace!!.contains("boom"))
  }

  @Test
  fun eventWithoutThrowableHasNoStackTrace() {
    LogEventCollector.clear()

    LogEventCollector.log(LogLevel.Info, "tag", "plain")

    assertNull(LogEventCollector.events.value.single().stackTrace)
  }

  @Test
  fun clearRemovesAllEvents() {
    LogEventCollector.log(LogLevel.Debug, "tag", "message")

    LogEventCollector.clear()

    assertTrue(LogEventCollector.events.value.isEmpty())
  }

  @Test
  fun assignsUniqueIds() {
    LogEventCollector.clear()

    LogEventCollector.log(LogLevel.Debug, "tag", "a")
    LogEventCollector.log(LogLevel.Debug, "tag", "b")

    val ids = LogEventCollector.events.value.map { it.id }
    assertEquals(ids.toSet().size, ids.size)
  }
}
