package com.livewire.plugin.logs.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.livewire.plugin.logs.data.LogEvent
import com.livewire.plugin.logs.formatTimestamp
import com.livewire.plugin.logs.ui.Close
import com.livewire.plugin.logs.ui.Icons
import com.livewire.ui.actions.ClickAction
import com.livewire.ui.graphics.RoundedCornerShape
import com.livewire.ui.layout.Alignment
import com.livewire.ui.layout.Column
import com.livewire.ui.layout.Row
import com.livewire.ui.modifier.LivewireModifier
import com.livewire.ui.modifier.copyClickable
import com.livewire.ui.modifier.fillMaxSize
import com.livewire.ui.modifier.fillMaxWidth
import com.livewire.ui.modifier.height
import com.livewire.ui.modifier.padding
import com.livewire.ui.modifier.thenIf
import com.livewire.ui.modifier.verticalScroll
import com.livewire.ui.theme.LivewireTheme
import com.livewire.ui.widget.CodeBlock
import com.livewire.ui.widget.HorizontalDivider
import com.livewire.ui.widget.Icon
import com.livewire.ui.widget.IconButton
import com.livewire.ui.widget.Spacer
import com.livewire.ui.widget.Surface
import com.livewire.ui.widget.Text

@Composable
internal fun LogDetailPane(
  event: LogEvent,
  onClose: ClickAction,
  modifier: LivewireModifier = LivewireModifier,
) {
  Column(
    modifier = modifier.fillMaxSize(),
  ) {
    // Header bar
    Surface(
      modifier = LivewireModifier.height(60.dp),
      shadowElevation = 4.dp,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = LivewireModifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp),
      ) {
        Text(
          text = "Log Detail",
          style = LivewireTheme.typography.titleSmall,
          modifier = LivewireModifier.weight(1f).padding(left = 8.dp),
        )
        IconButton(action = onClose) {
          Icon(imageVector = Icons.Close)
        }
      }
    }

    Column(
      modifier = LivewireModifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
    ) {
      Spacer(LivewireModifier.height(8.dp))

      OverviewRow(label = "Level", value = event.level.name)
      OverviewRow(label = "Time", value = formatTimestamp(event.timestamp))
      OverviewRow(label = "Tag", value = event.tag)

      HorizontalDivider(
        modifier = LivewireModifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
      )

      Text(
        text = "Message",
        style = LivewireTheme.typography.labelMedium,
        color = LivewireTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(LivewireModifier.height(4.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
        modifier = LivewireModifier
          .fillMaxWidth()
          .thenIf(event.stackTrace == null) { weight(1f) },
      ) {
        Column(
          modifier = LivewireModifier
            .fillMaxWidth()
            .verticalScroll()
            .padding(12.dp),
        ) {
          Text(
            text = event.message,
            style = LivewireTheme.typography.bodyMedium,
            modifier = LivewireModifier.copyClickable(event.message),
          )
        }
      }

      event.stackTrace?.let { stackTrace ->
        Spacer(LivewireModifier.height(8.dp))
        Text(
          text = "Stack trace",
          style = LivewireTheme.typography.labelMedium,
          color = LivewireTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(LivewireModifier.height(4.dp))
        CodeBlock(
          content = stackTrace,
          modifier = LivewireModifier
            .weight(1f)
            .fillMaxWidth(),
        )
      }

      Spacer(LivewireModifier.height(8.dp))
    }
  }
}

@Composable
private fun OverviewRow(
  label: String,
  value: String,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = LivewireModifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
  ) {
    Text(
      text = label,
      style = LivewireTheme.typography.labelMedium,
      color = LivewireTheme.colorScheme.onSurfaceVariant,
      modifier = LivewireModifier.padding(right = 8.dp),
    )
    Text(
      text = value,
      style = LivewireTheme.typography.bodyMedium,
      modifier = LivewireModifier.copyClickable(value),
    )
  }
}
