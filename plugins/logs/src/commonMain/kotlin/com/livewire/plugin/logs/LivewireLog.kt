package com.livewire.plugin.logs

import com.livewire.plugin.logs.data.LogEventCollector
import com.livewire.plugin.logs.data.LogLevel

object LivewireLog {

  fun v(tag: String, message: String, throwable: Throwable? = null) =
    LogEventCollector.log(LogLevel.Verbose, tag, message, throwable)

  fun d(tag: String, message: String, throwable: Throwable? = null) =
    LogEventCollector.log(LogLevel.Debug, tag, message, throwable)

  fun i(tag: String, message: String, throwable: Throwable? = null) =
    LogEventCollector.log(LogLevel.Info, tag, message, throwable)

  fun w(tag: String, message: String, throwable: Throwable? = null) =
    LogEventCollector.log(LogLevel.Warn, tag, message, throwable)

  fun e(tag: String, message: String, throwable: Throwable? = null) =
    LogEventCollector.log(LogLevel.Error, tag, message, throwable)
}
