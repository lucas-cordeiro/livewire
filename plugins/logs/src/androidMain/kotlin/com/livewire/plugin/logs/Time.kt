package com.livewire.plugin.logs

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal actual fun currentTimeMillis(): Long = System.currentTimeMillis()

private val timestampFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

internal actual fun formatTimestamp(epochMillis: Long): String =
  Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .toLocalTime()
    .format(timestampFormatter)
