package com.livewire.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livewire.plugin.logs.LogSessionViewer
import com.livewire.plugin.network.NetworkSessionViewer
import com.livewire.session.ImportedLogBundle
import com.livewire.session.ImportedSession
import com.livewire.ui.actions.LivewireAction
import com.livewire.ui.actions.LivewireActionDispatcher
import com.livewire.ui.actions.LocalLivewireActionDispatcher
import com.livewire.ui.actions.LocalLivewireActionObserver
import com.livewire.ui.actions.rememberLivewireActionController
import com.livewire.ui.composition.LivewireOutput
import com.livewire.ui.composition.livewireFlow
import com.livewire.ui.data.LayoutNodeSerializationStrategy
import com.livewire.ui.layout.LayoutNode
import com.livewire.ui.layout.HostDrawerSheet
import com.livewire.ui.snackbar.LocalSnackDispatcher
import com.livewire.ui.snackbar.rememberSnackbarDispatcher
import com.livewire.ui.theme.LivewireTheme
import com.livewire.host.ui.LayoutNodeContent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableSharedFlow

private val SessionViewerPluginIds = setOf("logs", "network")

@Composable
internal fun ImportedSessionUi(
  fileName: String,
  bundle: ImportedLogBundle,
  darkMode: Boolean,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val theme = remember { LivewireTheme() }
  var selectedSession by remember(bundle) { mutableStateOf(bundle.sessions.first()) }
  var selectedPlugin by remember(bundle) {
    mutableStateOf(
      bundle.sessions.first().metadata.plugins.firstOrNull { it.pluginId in SessionViewerPluginIds },
    )
  }

  MaterialTheme(
    colorScheme = if (darkMode) theme.darkColorScheme else theme.lightColorScheme,
  ) {
    Surface(modifier = modifier.fillMaxSize()) {
      Column(modifier = Modifier.fillMaxSize()) {
        ImportedSessionTopBar(
          fileName = fileName,
          sessions = bundle.sessions,
          selectedSession = selectedSession,
          onSelectSession = { selectedSession = it },
          onClose = onClose,
        )

        Row(modifier = Modifier.fillMaxSize()) {
          HostDrawerSheet(modifier = Modifier.fillMaxHeight()) {
            DrawerContent(
              expanded = true,
              selectedPlugin = selectedPlugin,
              availablePlugins = selectedSession.metadata.plugins,
              onPluginClick = { selectedPlugin = it },
            )
          }

          val plugin = selectedPlugin
          when {
            plugin == null -> ImportedSessionHint()

            plugin.pluginId == "logs" -> LocalDslPanel(
              contentKey = "${selectedSession.metadata.id}:logs",
              theme = theme,
              darkMode = darkMode,
              modifier = Modifier.weight(1f).fillMaxHeight(),
            ) {
              LogSessionViewer(selectedSession.logs)
            }

            plugin.pluginId == "network" -> LocalDslPanel(
              contentKey = "${selectedSession.metadata.id}:network",
              theme = theme,
              darkMode = darkMode,
              modifier = Modifier.weight(1f).fillMaxHeight(),
            ) {
              NetworkSessionViewer(selectedSession.network)
            }

            else -> LiveOnlyPluginDisclaimer(
              plugin = plugin,
              modifier = Modifier.weight(1f).fillMaxHeight(),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ImportedSessionTopBar(
  fileName: String,
  sessions: List<ImportedSession>,
  selectedSession: ImportedSession,
  onSelectSession: (ImportedSession) -> Unit,
  onClose: () -> Unit,
) {
  Surface(shadowElevation = 4.dp) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 12.dp),
    ) {
      Text(
        text = fileName,
        style = MaterialTheme.typography.titleSmall,
        maxLines = 1,
      )

      Text(
        text = "·",
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp),
      )

      var sessionMenuOpen by remember { mutableStateOf(false) }
      Box {
        TextButton(onClick = { sessionMenuOpen = true }) {
          Text("${formatStartedAt(selectedSession.metadata.startedAt)} ▾")
        }
        DropdownMenu(
          expanded = sessionMenuOpen,
          onDismissRequest = { sessionMenuOpen = false },
        ) {
          sessions.forEach { session ->
            DropdownMenuItem(
              text = {
                Column {
                  Text(formatStartedAt(session.metadata.startedAt))
                  Text(
                    text = "${session.logs.size} logs · ${session.network.size} requests · ${session.metadata.appId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              },
              onClick = {
                sessionMenuOpen = false
                onSelectSession(session)
              },
            )
          }
        }
      }

      Box(modifier = Modifier.weight(1f))

      Text(
        text = "Recorded session — read-only",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 8.dp),
      )

      TextButton(onClick = onClose) {
        Text("Close")
      }
    }
  }
}

@Composable
private fun ImportedSessionHint() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize(),
  ) {
    Text(
      text = "Select a plugin to browse this session",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun LiveOnlyPluginDisclaimer(
  plugin: PluginInfo,
  modifier: Modifier = Modifier,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      plugin.icon?.let { icon ->
        Icon(
          imageVector = icon.toImageVector(),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(48.dp),
        )
      }
      Text(
        text = plugin.title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 12.dp),
      )
      Text(
        text = "This plugin needs a live connection — it can't render from a recorded session.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
      )
    }
  }
}

@Composable
private fun LocalDslPanel(
  contentKey: Any,
  theme: LivewireTheme,
  darkMode: Boolean,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val controller = rememberLivewireActionController()
  val strategy = remember { LayoutNodeSerializationStrategy.Default }
  val resync = remember(contentKey) { MutableSharedFlow<Unit>(extraBufferCapacity = 1) }
  var tree by remember(contentKey) { mutableStateOf<LayoutNode?>(null) }

  LaunchedEffect(contentKey) {
    livewireFlow(strategy, resync) {
      CompositionLocalProvider(LocalLivewireActionObserver provides controller) {
        LivewireTheme(theme = theme, darkMode = darkMode) {
          content()
        }
      }
    }.collect { output ->
      when (output) {
        is LivewireOutput.FullTree -> {
          tree = strategy.decodeFromByteArray(strategy.encodeToByteArray(output.root))
        }

        is LivewireOutput.Patches -> {
          resync.emit(Unit)
        }
      }
    }
  }

  val dispatcher = remember(controller) {
    object : LivewireActionDispatcher {
      override suspend fun dispatch(action: LivewireAction) {
        controller.dispatch(action)
      }
    }
  }
  val snackbarHostState = remember { SnackbarHostState() }
  val snackbarDispatcher = rememberSnackbarDispatcher(snackbarHostState)

  Box(modifier = modifier) {
    tree?.let { node ->
      CompositionLocalProvider(
        LocalLivewireActionDispatcher provides dispatcher,
        LocalSnackDispatcher provides snackbarDispatcher,
      ) {
        LayoutNodeContent(node = node, modifier = Modifier.fillMaxSize())
      }
    }
  }
}

private val startedAtFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm:ss")

private fun formatStartedAt(epochMillis: Long): String =
  Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .format(startedAtFormatter)
