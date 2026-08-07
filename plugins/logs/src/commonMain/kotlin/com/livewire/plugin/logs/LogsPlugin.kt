package com.livewire.plugin.logs

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.livewire.plugin.logs.composables.LogDetailPane
import com.livewire.plugin.logs.composables.LogListItem
import com.livewire.plugin.logs.composables.LogsToolbar
import com.livewire.plugin.logs.ui.Icons
import com.livewire.plugin.logs.ui.Logs
import com.livewire.ui.Plugin
import com.livewire.ui.PluginInfo
import com.livewire.ui.actions.clickAction
import com.livewire.ui.actions.valueChangeAction
import com.livewire.ui.layout.Column
import com.livewire.ui.layout.Row
import com.livewire.ui.modifier.LivewireModifier
import com.livewire.ui.modifier.animateContentSize
import com.livewire.ui.modifier.fillMaxHeight
import com.livewire.ui.modifier.fillMaxSize
import com.livewire.ui.modifier.fillMaxWidth
import com.livewire.ui.modifier.height
import com.livewire.ui.modifier.verticalScroll
import com.livewire.ui.widget.AnimatedVisibility
import com.livewire.ui.widget.ResizableSurface
import com.livewire.ui.widget.ResizeAnchor
import com.livewire.ui.widget.Spacer

class LogsPlugin : Plugin {

  private val presenter = LogsPresenter()

  override val info: PluginInfo = PluginInfo(
    pluginId = "logs",
    title = "Logs",
    icon = Icons.Logs,
  )

  @Composable
  override fun Content() {
    val state = presenter.present()

    Row(
      modifier = LivewireModifier.fillMaxSize(),
    ) {
      // Log list (left pane)
      Column(
        modifier = LivewireModifier
          .weight(1f)
          .fillMaxHeight()
          .animateContentSize(),
      ) {
        LogsToolbar(
          filterText = state.filterText,
          minLevel = state.minLevel,
          onFilterChange = valueChangeAction {
            state.eventSink(LogsUiEvent.UpdateFilter(it))
          },
          onMinLevelChange = { level ->
            state.eventSink(LogsUiEvent.UpdateMinLevel(level))
          },
          onClearAll = clickAction {
            state.eventSink(LogsUiEvent.ClearAll)
          },
        )

        Column(
          modifier = LivewireModifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(),
        ) {
          Spacer(LivewireModifier.height(8.dp))
          state.events.forEach { event ->
            LogListItem(
              event = event,
              isSelected = state.selectedEvent?.id == event.id,
              onClick = clickAction {
                state.eventSink(LogsUiEvent.SelectEvent(event))
              },
            )
          }
          Spacer(LivewireModifier.height(8.dp))
        }
      }

      // Detail pane (right side)
      AnimatedVisibility(
        visible = state.selectedEvent != null,
        modifier = LivewireModifier.fillMaxHeight(),
      ) {
        ResizableSurface(
          anchor = ResizeAnchor.Start,
          initialSize = 400.dp,
          minSize = 200.dp,
          maxSize = 600.dp,
          shadowElevation = 2.dp,
          modifier = LivewireModifier
            .fillMaxHeight(),
        ) {
          state.selectedEvent?.let { event ->
            LogDetailPane(
              event = event,
              onClose = clickAction {
                state.eventSink(LogsUiEvent.ClearSelection)
              },
            )
          }
        }
      }
    }
  }
}
