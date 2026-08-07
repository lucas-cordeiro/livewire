package com.livewire.sessions

import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice

actual fun sessionEnvironment(): SessionEnvironment? {
  val documents = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    .firstOrNull() as? String
    ?: return null

  return SessionEnvironment(
    fileSystem = FileSystem.SYSTEM,
    root = documents.toPath() / "livewire" / "log-sessions",
    appId = NSBundle.mainBundle.bundleIdentifier ?: "ios-app",
    platform = "ios",
    osVersion = "${UIDevice.currentDevice.systemName} ${UIDevice.currentDevice.systemVersion}",
  )
}
