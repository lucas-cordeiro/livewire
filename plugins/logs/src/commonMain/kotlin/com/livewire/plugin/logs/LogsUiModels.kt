package com.livewire.plugin.logs

import androidx.compose.runtime.Immutable
import com.livewire.plugin.logs.data.LogEvent
import com.livewire.plugin.logs.data.LogLevel

@Immutable
data class LogsUiState(
  val events: List<LogEvent>,
  val selectedEvent: LogEvent?,
  val filterText: String,
  val minLevel: LogLevel,
  val eventSink: (LogsUiEvent) -> Unit,
)

sealed interface LogsUiEvent {
  data class SelectEvent(val event: LogEvent) : LogsUiEvent
  data object ClearSelection : LogsUiEvent
  data class UpdateFilter(val text: String) : LogsUiEvent
  data class UpdateMinLevel(val level: LogLevel) : LogsUiEvent
  data object ClearAll : LogsUiEvent
}
