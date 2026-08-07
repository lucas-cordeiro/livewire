package com.livewire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livewire.client.LivewireClient
import com.livewire.sessions.LivewireSessions

class MainActivity : ComponentActivity() {

  private val livewireClient: LivewireClient = ServiceLocator.livewireClient

  @OptIn(InternalComposeApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
          LivewireApp(
            livewireClient = livewireClient,
            settings = ServiceLocator.settingsDataStore,
          )
          TextButton(
            onClick = { LivewireSessions.launch(this@MainActivity) },
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(16.dp),
          ) {
            Text("Log sessions")
          }
        }
      }
    }
  }
}
