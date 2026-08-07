package com.livewire.plugin.logs.data

import com.livewire.plugin.logs.currentTimeMillis
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalAtomicApi::class)
object LogEventCollector {

  private const val MAX_EVENTS = 2000

  private val idCounter = AtomicLong(0L)

  private val _events = MutableStateFlow<List<LogEvent>>(emptyList())
  val events: StateFlow<List<LogEvent>> = _events.asStateFlow()

  fun log(
    level: LogLevel,
    tag: String,
    message: String,
    throwable: Throwable? = null,
  ) {
    val event = LogEvent(
      id = "log-${idCounter.fetchAndAdd(1)}",
      timestamp = currentTimeMillis(),
      level = level,
      tag = tag,
      message = message,
      stackTrace = throwable?.stackTraceToString(),
    )
    _events.update { current ->
      (listOf(event) + current).take(MAX_EVENTS)
    }
  }

  fun clear() {
    _events.update { emptyList() }
  }
}
