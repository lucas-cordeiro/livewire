package com.livewire.plugin.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livewire.plugin.logs.composables.LogsPanel
import com.livewire.plugin.logs.data.LogEvent
import com.livewire.ui.modifier.LivewireModifier
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LogSessionViewer(
  events: List<LogEvent>,
  modifier: LivewireModifier = LivewireModifier,
) {
  val presenter = remember(events) {
    LogsPresenter(
      source = MutableStateFlow(events.sortedByDescending { it.timestamp }),
      onClearAll = null,
    )
  }
  LogsPanel(presenter.present(), modifier)
}
