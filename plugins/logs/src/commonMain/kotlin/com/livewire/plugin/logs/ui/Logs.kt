package com.livewire.plugin.logs.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val Icons.Logs: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
  ImageVector.Builder(
    name = "Logs",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
  ).apply {
    path(fill = SolidColor(Color.Black)) {
      moveTo(4f, 5f)
      lineTo(20f, 5f)
      lineTo(20f, 7f)
      lineTo(4f, 7f)
      close()
      moveTo(4f, 11f)
      lineTo(20f, 11f)
      lineTo(20f, 13f)
      lineTo(4f, 13f)
      close()
      moveTo(4f, 17f)
      lineTo(14f, 17f)
      lineTo(14f, 19f)
      lineTo(4f, 19f)
      close()
    }
  }.build()
}
