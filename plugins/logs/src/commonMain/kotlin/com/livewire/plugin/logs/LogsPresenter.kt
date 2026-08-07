package com.livewire.plugin.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.livewire.plugin.logs.data.LogEvent
import com.livewire.plugin.logs.data.LogEventCollector
import com.livewire.plugin.logs.data.LogLevel

class LogsPresenter {

  private var selectedEvent by mutableStateOf<LogEvent?>(null)
  private var filterText by mutableStateOf("")
  private var minLevel by mutableStateOf(LogLevel.Verbose)

  @Composable
  fun present(): LogsUiState {
    val allEvents by LogEventCollector.events.collectAsState()

    val visibleEvents = allEvents.filter { event ->
      event.level.ordinal >= minLevel.ordinal && matchesFilter(event)
    }

    // Keep selectedEvent in sync — clear if it's no longer in the list
    val currentSelected = selectedEvent?.let { selected ->
      allEvents.find { it.id == selected.id }
    }

    return LogsUiState(
      events = visibleEvents,
      selectedEvent = currentSelected,
      filterText = filterText,
      minLevel = minLevel,
    ) { event ->
      when (event) {
        is LogsUiEvent.SelectEvent -> {
          selectedEvent = event.event
        }

        LogsUiEvent.ClearSelection -> {
          selectedEvent = null
        }

        is LogsUiEvent.UpdateFilter -> {
          filterText = event.text
        }

        is LogsUiEvent.UpdateMinLevel -> {
          minLevel = event.level
        }

        LogsUiEvent.ClearAll -> {
          LogEventCollector.clear()
          selectedEvent = null
          filterText = ""
        }
      }
    }
  }

  private fun matchesFilter(event: LogEvent): Boolean {
    if (filterText.isBlank()) return true
    val query = filterText.lowercase()
    return event.tag.lowercase().contains(query) ||
      event.message.lowercase().contains(query)
  }
}
