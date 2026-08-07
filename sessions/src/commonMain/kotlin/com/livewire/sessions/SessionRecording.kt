@file:OptIn(FlowPreview::class)

package com.livewire.sessions

import com.livewire.ui.PluginInfo
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okio.FileSystem
import okio.Path

@OptIn(ExperimentalAtomicApi::class)
object SessionRecording {

  private val started = AtomicBoolean(false)

  fun start(maxSessions: Int, plugins: List<PluginInfo>) {
    if (!started.compareAndSet(expectedValue = false, newValue = true)) return
    val environment = sessionEnvironment() ?: return

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    scope.launch {
      val sessionDir = runCatching {
        val fileSystem = environment.fileSystem
        fileSystem.createDirectories(environment.root)
        prune(fileSystem, environment.root, keep = maxSessions - 1)

        val startedAt = currentTimeMillis()
        val metadata = SessionMetadata(
          id = "session-$startedAt",
          startedAt = startedAt,
          appId = environment.appId,
          platform = environment.platform,
          osVersion = environment.osVersion,
          plugins = plugins,
        )

        val dir = environment.root / metadata.id
        fileSystem.createDirectories(dir)
        fileSystem.write(dir / SessionStore.MetadataFileName) {
          writeUtf8(SessionStore.json.encodeToString(SessionMetadata.serializer(), metadata))
        }
        dir
      }.getOrNull() ?: return@launch

      SessionChannels.channels.collect { channels ->
        channels.forEach { channel ->
          if (recordingChannels.add(channel.fileName)) {
            scope.launch {
              channel.changes
                .onStart { emit(Unit) }
                .debounce(1_000)
                .collect {
                  writeSnapshot(environment.fileSystem, sessionDir, channel)
                }
            }
          }
        }
      }
    }
  }

  private val recordingChannels = mutableSetOf<String>()

  private fun writeSnapshot(fileSystem: FileSystem, sessionDir: Path, channel: SessionChannel) {
    runCatching {
      val target = sessionDir / channel.fileName
      val temp = sessionDir / "${channel.fileName}.tmp"
      fileSystem.write(temp) {
        channel.snapshotLines().forEach { line ->
          writeUtf8(line)
          writeUtf8("\n")
        }
      }
      fileSystem.atomicMove(temp, target)
    }
  }

  private fun prune(fileSystem: FileSystem, root: Path, keep: Int) {
    val sessions = runCatching { fileSystem.list(root) }.getOrDefault(emptyList())
      .filter { runCatching { fileSystem.metadata(it).isDirectory }.getOrDefault(false) }
      .sortedBy { it.name }

    val excess = sessions.size - keep.coerceAtLeast(0)
    if (excess > 0) {
      sessions.take(excess).forEach { session ->
        runCatching { fileSystem.deleteRecursively(session) }
      }
    }
  }
}
