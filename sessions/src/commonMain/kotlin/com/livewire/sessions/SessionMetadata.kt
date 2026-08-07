package com.livewire.sessions

import com.livewire.ui.PluginInfo
import kotlinx.serialization.Serializable

@Serializable
data class SessionMetadata(
  val id: String,
  val startedAt: Long,
  val appId: String,
  val platform: String,
  val osVersion: String,
  val plugins: List<PluginInfo> = emptyList(),
  val formatVersion: Int = FORMAT_VERSION,
) {
  companion object {
    const val FORMAT_VERSION = 2
  }
}

data class RecordedSession(
  val metadata: SessionMetadata,
  val directoryName: String,
)
