package com.livewire.plugin.logs

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.Foundation.timeIntervalSince1970

internal actual fun currentTimeMillis(): Long =
  (NSDate().timeIntervalSince1970 * 1000).toLong()

private val timestampFormatter = NSDateFormatter().apply {
  dateFormat = "HH:mm:ss.SSS"
  timeZone = NSTimeZone.localTimeZone
}

internal actual fun formatTimestamp(epochMillis: Long): String =
  timestampFormatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0))
