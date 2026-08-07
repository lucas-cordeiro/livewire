package com.livewire.sessions

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SessionsActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val store = sessionEnvironment()?.let { SessionStore(it.fileSystem, it.root) }

    setContent {
      MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
      ) {
        SessionsScreen(
          rows = remember {
            store?.sessions().orEmpty().map { session ->
              SessionRowData(session, store?.channelCounts(session).orEmpty())
            }
          },
          onShare = { session -> SessionExporter.share(this, session) },
          onDownload = { session -> downloadSession(session) },
        )
      }
    }
  }

  private fun downloadSession(session: RecordedSession) {
    lifecycleScope.launch {
      val savedTo = withContext(Dispatchers.IO) {
        SessionExporter.download(this@SessionsActivity, session)
      }
      val message = savedTo?.let { "Saved to $it" } ?: "Could not save session"
      Toast.makeText(this@SessionsActivity, message, Toast.LENGTH_LONG).show()
    }
  }
}

private data class SessionRowData(
  val session: RecordedSession,
  val channelCounts: Map<String, Int>,
)

@Composable
private fun SessionsScreen(
  rows: List<SessionRowData>,
  onShare: (RecordedSession) -> Unit,
  onDownload: (RecordedSession) -> Unit,
) {
  Surface(modifier = Modifier.fillMaxSize()) {
    Box(modifier = Modifier.safeDrawingPadding()) {
      SessionsList(rows = rows, onShare = onShare, onDownload = onDownload)
    }
  }
}

@Composable
private fun SessionsList(
  rows: List<SessionRowData>,
  onShare: (RecordedSession) -> Unit,
  onDownload: (RecordedSession) -> Unit,
) {
  if (rows.isEmpty()) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize(),
    ) {
      Text(
        text = "No recorded sessions",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    return
  }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    items(rows, key = { it.session.directoryName }) { row ->
      SessionRow(
        row = row,
        onShare = { onShare(row.session) },
        onDownload = { onDownload(row.session) },
      )
    }
  }
}

@Composable
private fun SessionRow(
  row: SessionRowData,
  onShare: () -> Unit,
  onDownload: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = formatStartedAt(row.session.metadata.startedAt),
        style = MaterialTheme.typography.titleSmall,
      )
      Text(
        text = buildString {
          row.channelCounts.entries.forEach { (channel, count) ->
            append("$count $channel · ")
          }
          append(row.session.metadata.platform)
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    TextButton(onClick = onDownload) {
      Text("Download")
    }
    TextButton(onClick = onShare) {
      Text("Share")
    }
  }
}

private val startedAtFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm:ss")

private fun formatStartedAt(epochMillis: Long): String =
  Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .format(startedAtFormatter)
