package com.livewire.sessions

import android.os.Build
import com.livewire.ContextHolder
import okio.FileSystem
import okio.Path.Companion.toPath

actual fun sessionEnvironment(): SessionEnvironment? {
  val context = runCatching { ContextHolder.appContext }.getOrNull() ?: return null
  return SessionEnvironment(
    fileSystem = FileSystem.SYSTEM,
    root = context.filesDir.absolutePath.toPath() / "livewire" / "log-sessions",
    appId = context.packageName,
    platform = "android",
    osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
  )
}
