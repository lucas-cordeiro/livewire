package com.livewire.plugin.logs

import kotlin.test.Test
import kotlin.test.assertTrue

class TimeTest {

  @Test
  fun formatsTimestampAsLocalWallClock() {
    val formatted = formatTimestamp(currentTimeMillis())
    assertTrue(
      formatted.matches(Regex("""\d{2}:\d{2}:\d{2}\.\d{3}""")),
      "unexpected timestamp format: $formatted",
    )
  }
}
