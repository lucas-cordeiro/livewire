package com.livewire.plugin.logs.data.session

import com.livewire.plugin.logs.data.LogEvent
import com.livewire.plugin.logs.data.LogEventCollector
import com.livewire.sessions.SessionChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal object LogsSessionChannel : SessionChannel {

  override val fileName: String = LogSessionEvents.FileName

  override val changes: Flow<Unit> = LogEventCollector.events.drop(1).map { }

  override fun snapshotLines(): List<String> =
    LogEventCollector.events.value.asReversed().map { event ->
      LogSessionEvents.json.encodeToString(LogEvent.serializer(), event)
    }
}

object LogSessionEvents {

  const val FileName = "logs.jsonl"

  internal val json = Json {
    ignoreUnknownKeys = true
  }

  fun decode(lines: List<String>): List<LogEvent> =
    lines.mapNotNull { line ->
      runCatching { json.decodeFromString(LogEvent.serializer(), line) }.getOrNull()
    }
}
