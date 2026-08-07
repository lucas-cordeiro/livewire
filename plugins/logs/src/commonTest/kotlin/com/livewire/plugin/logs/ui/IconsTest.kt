package com.livewire.plugin.logs.ui

import com.livewire.ui.graphics.toVectorIcon
import kotlin.test.Test
import kotlin.test.assertEquals

class IconsTest {

  @Test
  fun iconsBuildAndRoundTripThroughVectorIcon() {
    listOf(Icons.Logs, Icons.Delete, Icons.Close).forEach { icon ->
      assertEquals(icon, icon.toVectorIcon().toImageVector())
    }
  }
}
