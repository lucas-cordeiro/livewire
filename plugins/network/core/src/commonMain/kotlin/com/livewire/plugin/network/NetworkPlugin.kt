package com.livewire.plugin.network

import androidx.compose.runtime.Composable
import com.livewire.plugin.network.composables.NetworkPanel
import com.livewire.plugin.network.data.NetworkSessionChannel
import com.livewire.plugin.network.ui.Icons
import com.livewire.plugin.network.ui.Network
import com.livewire.sessions.SessionChannels
import com.livewire.ui.Plugin
import com.livewire.ui.PluginInfo

class NetworkPlugin(
  configure: NetworkPluginBuilder.() -> Unit = {},
) : Plugin {

  private val config = NetworkPluginBuilder().apply(configure)

  private val presenter = NetworkPresenter()

  init {
    SessionChannels.register(NetworkSessionChannel)
  }

  override val info: PluginInfo = PluginInfo(
    pluginId = "network",
    title = "Network",
    icon = Icons.Network,
  )

  @Composable
  override fun Content() {
    NetworkPanel(
      state = presenter.present(),
      requestPathMaxLines = config.requestPathMaxLines,
    )
  }
}
