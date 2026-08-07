package com.livewire.plugin.logs.data

import kotlinx.serialization.Serializable

@Serializable
enum class LogLevel(val label: String) {
  Verbose("V"),
  Debug("D"),
  Info("I"),
  Warn("W"),
  Error("E"),
}

@Serializable
data class LogEvent(
  val id: String,
  val timestamp: Long,
  val level: LogLevel,
  val tag: String,
  val message: String,
  val stackTrace: String? = null,
)
