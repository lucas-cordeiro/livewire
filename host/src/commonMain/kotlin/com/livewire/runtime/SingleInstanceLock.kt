package com.livewire.runtime

import java.net.InetAddress
import java.net.ServerSocket

object SingleInstanceLock {

  private const val LockPort = 38309

  private var socket: ServerSocket? = null

  fun acquire(): Boolean {
    if (socket != null) return true

    return runCatching {
      socket = ServerSocket(LockPort, 1, InetAddress.getByName("127.0.0.1"))
      true
    }.getOrDefault(false)
  }

  fun release() {
    runCatching { socket?.close() }
    socket = null
  }
}
