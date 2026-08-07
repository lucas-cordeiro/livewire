package com.livewire.plugin.logs

import androidx.compose.runtime.Composable
import com.livewire.plugin.logs.composables.LogsPanel
import com.livewire.plugin.logs.data.session.LogsSessionChannel
import com.livewire.plugin.logs.ui.Icons
import com.livewire.plugin.logs.ui.Logs
import com.livewire.sessions.SessionChannels
import com.livewire.ui.Plugin
import com.livewire.ui.PluginInfo

class LogsPlugin : Plugin {

  private val presenter = LogsPresenter()

  init {
    SessionChannels.register(LogsSessionChannel)
  }

  override val info: PluginInfo = PluginInfo(
    pluginId = "logs",
    title = "Logs",
    icon = Icons.Logs,
  )

  @Composable
  override fun Content() {
    LogsPanel(presenter.present())
  }
}
