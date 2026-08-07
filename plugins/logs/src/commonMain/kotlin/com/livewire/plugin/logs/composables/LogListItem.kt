package com.livewire.plugin.logs.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.livewire.plugin.logs.data.LogEvent
import com.livewire.plugin.logs.data.LogLevel
import com.livewire.plugin.logs.formatTimestamp
import com.livewire.ui.actions.ClickAction
import com.livewire.ui.graphics.RoundedCornerShape
import com.livewire.ui.layout.Alignment
import com.livewire.ui.layout.Box
import com.livewire.ui.layout.Row
import com.livewire.ui.modifier.LivewireModifier
import com.livewire.ui.modifier.fillMaxWidth
import com.livewire.ui.modifier.padding
import com.livewire.ui.modifier.size
import com.livewire.ui.modifier.width
import com.livewire.ui.theme.LivewireTheme
import com.livewire.ui.widget.Surface
import com.livewire.ui.widget.Text

@Composable
internal fun LogListItem(
  event: LogEvent,
  isSelected: Boolean,
  onClick: ClickAction,
  modifier: LivewireModifier = LivewireModifier,
) {
  Surface(
    onClick = onClick,
    color = if (isSelected) LivewireTheme.colorScheme.surfaceContainerHighest else null,
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
      .padding(
        horizontal = 8.dp,
        vertical = 2.dp,
      )
      .fillMaxWidth(),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = LivewireModifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
      // Timestamp
      Text(
        text = formatTimestamp(event.timestamp),
        style = LivewireTheme.typography.labelSmall,
        color = LivewireTheme.colorScheme.onSurfaceVariant,
        modifier = LivewireModifier.padding(right = 8.dp),
      )

      // Level badge
      Box(
        contentAlignment = Alignment.Center,
        modifier = LivewireModifier.size(28.dp),
      ) {
        Text(
          text = event.level.label,
          style = LivewireTheme.typography.labelMedium,
          fontWeight = 700,
          color = levelColor(event.level),
        )
      }

      // Tag
      Text(
        text = event.tag,
        style = LivewireTheme.typography.labelMedium,
        fontWeight = 700,
        maxLines = 1,
        modifier = LivewireModifier.width(140.dp),
      )

      // Message
      Text(
        text = event.message,
        style = LivewireTheme.typography.bodySmall,
        color = LivewireTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        modifier = LivewireModifier.weight(1f),
      )
    }
  }
}

internal fun levelColor(level: LogLevel): Color = when (level) {
  LogLevel.Verbose -> Color.Gray
  LogLevel.Debug -> Color(0xFF2196F3)
  LogLevel.Info -> Color(0xFF4CAF50)
  LogLevel.Warn -> Color(0xFFFF9800)
  LogLevel.Error -> Color(0xFFF44336)
}

