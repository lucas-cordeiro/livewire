package com.livewire.plugin.logs.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.unit.dp
import com.livewire.plugin.logs.data.LogLevel
import com.livewire.plugin.logs.ui.Delete
import com.livewire.plugin.logs.ui.Icons
import com.livewire.ui.actions.ClickAction
import com.livewire.ui.actions.ValueChangeAction
import com.livewire.ui.actions.clickAction
import com.livewire.ui.graphics.CircleShape
import com.livewire.ui.graphics.RoundedCornerShape
import com.livewire.ui.layout.Alignment
import com.livewire.ui.layout.Column
import com.livewire.ui.layout.Row
import com.livewire.ui.modifier.LivewireModifier
import com.livewire.ui.modifier.background
import com.livewire.ui.modifier.fillMaxWidth
import com.livewire.ui.modifier.padding
import com.livewire.ui.modifier.width
import com.livewire.ui.theme.LivewireTheme
import com.livewire.ui.widget.BasicTextField
import com.livewire.ui.widget.Button
import com.livewire.ui.widget.ButtonShapes
import com.livewire.ui.widget.ButtonSize
import com.livewire.ui.widget.ButtonStyle
import com.livewire.ui.widget.Chip
import com.livewire.ui.widget.Icon
import com.livewire.ui.widget.Spacer
import com.livewire.ui.widget.Surface
import com.livewire.ui.widget.Text

@Composable
internal fun LogsToolbar(
  filterText: String,
  minLevel: LogLevel,
  onFilterChange: ValueChangeAction,
  onMinLevelChange: (LogLevel) -> Unit,
  onClearAll: ClickAction?,
  modifier: LivewireModifier = LivewireModifier,
) {
  Column(
    modifier = modifier
      .background(LivewireTheme.colorScheme.surfaceContainerHigh)
      .fillMaxWidth(),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = LivewireModifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
    ) {
      Surface(
        modifier = LivewireModifier
          .weight(1f)
          .padding(
            vertical = 8.dp,
          ),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
      ) {
        BasicTextField(
          initialValue = filterText,
          onValueChange = onFilterChange,
          placeholder = "Filter by tag or message…",
          singleLine = true,
          textStyle = LivewireTheme.typography.bodyMedium,
          modifier = LivewireModifier
            .fillMaxWidth()
            .padding(12.dp),
        )
      }

      if (onClearAll != null) {
        Spacer(LivewireModifier.width(8.dp))

        Button(
          action = onClearAll,
          size = ButtonSize.Small,
          style = ButtonStyle.Tonal,
          shapes = ButtonShapes(
            shape = RoundedCornerShape(8.dp),
            pressedShape = CircleShape,
          )
        ) {
          Icon(imageVector = Icons.Delete)
          Text("Clear")
        }
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = LivewireModifier
        .fillMaxWidth()
        .padding(left = 8.dp, right = 8.dp, bottom = 8.dp),
    ) {
      LogLevel.entries.forEach { level ->
        key(level.name) {
          Chip(
            label = level.name,
            selected = minLevel == level,
            action = clickAction {
              onMinLevelChange(level)
            },
          )
        }
        Spacer(LivewireModifier.width(4.dp))
      }
    }
  }
}
