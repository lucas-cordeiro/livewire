package com.livewire.plugin.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.livewire.plugin.network.data.NetworkEvent
import com.livewire.plugin.network.data.NetworkEventCollector
import kotlinx.coroutines.flow.StateFlow

class NetworkPresenter(
  private val source: StateFlow<List<NetworkEvent>> = NetworkEventCollector.events,
  private val onClearAll: (() -> Unit)? = { NetworkEventCollector.clear() },
) {

  private var selectedEvent by mutableStateOf<NetworkEvent?>(null)
  private var filterText by mutableStateOf("")
  private var expandedSections by mutableStateOf(DefaultExpandedSections)

  @Composable
  fun present(): NetworkUiState {
    val allEvents by source.collectAsState()

    val filteredEvents = if (filterText.isBlank()) {
      allEvents
    } else {
      val query = filterText.lowercase()
      allEvents.filter { event ->
        event.request.url.lowercase().contains(query) ||
          event.request.method.lowercase().contains(query) ||
          event.response?.statusCode?.toString()?.contains(query) == true
      }
    }

    // Keep selectedEvent in sync — clear if it's no longer in the list
    val currentSelected = selectedEvent?.let { selected ->
      allEvents.find { it.id == selected.id }
    }

    return NetworkUiState(
      events = filteredEvents,
      selectedEvent = currentSelected,
      filterText = filterText,
      expandedSections = expandedSections,
      canClear = onClearAll != null,
    ) { event ->
      when (event) {
        is NetworkUiEvent.SelectEvent -> {
          selectedEvent = event.event
          expandedSections = DefaultExpandedSections
        }

        NetworkUiEvent.ClearSelection -> {
          selectedEvent = null
          expandedSections = DefaultExpandedSections
        }

        is NetworkUiEvent.UpdateFilter -> {
          filterText = event.text
        }

        NetworkUiEvent.ClearAll -> {
          onClearAll?.invoke()
          selectedEvent = null
          filterText = ""
          expandedSections = DefaultExpandedSections
        }

        is NetworkUiEvent.ToggleDetailSection -> {
          expandedSections = if (event.section in expandedSections) {
            expandedSections - event.section
          } else {
            expandedSections + event.section
          }
        }
      }
    }
  }

  companion object {
    private val DefaultExpandedSections = setOf(
      DetailSection.RequestBody,
      DetailSection.ResponseBody,
    )
  }
}
