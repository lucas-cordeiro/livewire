package com.livewire.sessions

import okio.FileSystem
import okio.Path.Companion.toPath

actual fun sessionEnvironment(): SessionEnvironment? {
  val home = System.getProperty("user.home") ?: return null
  val appId = System.getProperty("sun.java.command")
    ?.substringBefore(' ')
    ?.substringAfterLast('.')
    ?.removeSuffix("Kt")
    ?.ifBlank { null }
    ?: "jvm-app"

  return SessionEnvironment(
    fileSystem = FileSystem.SYSTEM,
    root = home.toPath() / ".livewire" / "log-sessions" / appId,
    appId = appId,
    platform = "desktop",
    osVersion = "${System.getProperty("os.name")} ${System.getProperty("os.version")}",
  )
}
