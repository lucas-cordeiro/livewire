package com.livewire.sessions

import okio.FileSystem
import okio.Path

class SessionEnvironment(
  val fileSystem: FileSystem,
  val root: Path,
  val appId: String,
  val platform: String,
  val osVersion: String,
)

expect fun sessionEnvironment(): SessionEnvironment?
