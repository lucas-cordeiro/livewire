package com.livewire.plugin.network.data

import com.livewire.sessions.SessionChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

internal object NetworkSessionChannel : SessionChannel {

  override val fileName: String = NetworkSessionEvents.FileName

  override val changes: Flow<Unit> = NetworkEventCollector.events.drop(1).map { }

  override fun snapshotLines(): List<String> =
    NetworkEventCollector.events.value.asReversed().map { event ->
      NetworkSessionEvents.json.encodeToString(NetworkEvent.serializer(), event)
    }
}

object NetworkSessionEvents {

  const val FileName = "network.jsonl"

  internal val json = Json {
    ignoreUnknownKeys = true
  }

  fun decode(lines: List<String>): List<NetworkEvent> =
    lines.mapNotNull { line ->
      runCatching { json.decodeFromString(NetworkEvent.serializer(), line) }.getOrNull()
    }
}
