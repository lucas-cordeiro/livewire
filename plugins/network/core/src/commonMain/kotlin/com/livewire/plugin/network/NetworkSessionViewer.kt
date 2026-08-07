package com.livewire.plugin.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.livewire.plugin.network.composables.NetworkPanel
import com.livewire.plugin.network.data.NetworkEvent
import com.livewire.ui.modifier.LivewireModifier
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun NetworkSessionViewer(
  events: List<NetworkEvent>,
  modifier: LivewireModifier = LivewireModifier,
) {
  val presenter = remember(events) {
    NetworkPresenter(
      source = MutableStateFlow(events.sortedByDescending { it.request.timestamp }),
      onClearAll = null,
    )
  }
  NetworkPanel(
    state = presenter.present(),
    requestPathMaxLines = NetworkPluginBuilder().requestPathMaxLines,
    modifier = modifier,
  )
}
