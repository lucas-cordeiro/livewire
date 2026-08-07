package com.livewire.sessions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface SessionChannel {
  val fileName: String
  val changes: Flow<Unit>
  fun snapshotLines(): List<String>
}

object SessionChannels {

  private val _channels = MutableStateFlow<List<SessionChannel>>(emptyList())
  val channels: StateFlow<List<SessionChannel>> = _channels.asStateFlow()

  fun register(channel: SessionChannel) {
    _channels.update { current ->
      if (current.any { it.fileName == channel.fileName }) current else current + channel
    }
  }
}
