package com.livewire.session

import com.livewire.plugin.logs.data.LogEvent
import com.livewire.plugin.logs.data.session.LogSessionEvents
import com.livewire.plugin.network.data.NetworkEvent
import com.livewire.plugin.network.data.NetworkSessionEvents
import com.livewire.sessions.SessionMetadata
import com.livewire.sessions.SessionStore
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipInputStream
import okio.FileSystem
import okio.Path.Companion.toPath

internal data class ImportedLogBundle(
  val sessions: List<ImportedSession>,
)

internal data class ImportedSession(
  val metadata: SessionMetadata,
  val logs: List<LogEvent>,
  val network: List<NetworkEvent>,
)

internal object LogBundleImporter {

  fun import(zipFile: File): Result<ImportedLogBundle> = runCatching {
    val tempDir = Files.createTempDirectory("livewire-session-import").toFile()

    ZipInputStream(zipFile.inputStream().buffered()).use { zip ->
      while (true) {
        val entry = zip.nextEntry ?: break
        if (entry.isDirectory) continue

        val target = File(tempDir, entry.name)
        if (!target.canonicalPath.startsWith(tempDir.canonicalPath + File.separator)) continue

        target.parentFile?.mkdirs()
        target.outputStream().use { output -> zip.copyTo(output) }
      }
    }

    val sessionsRoot = File(tempDir, "sessions").takeIf { it.isDirectory } ?: tempDir
    val store = SessionStore(FileSystem.SYSTEM, sessionsRoot.absolutePath.toPath())
    val sessions = store.sessions().map { session ->
      ImportedSession(
        metadata = session.metadata,
        logs = LogSessionEvents.decode(store.lines(session, LogSessionEvents.FileName)),
        network = NetworkSessionEvents.decode(store.lines(session, NetworkSessionEvents.FileName)),
      )
    }

    check(sessions.isNotEmpty()) { "No Livewire sessions found in ${zipFile.name}" }
    ImportedLogBundle(sessions)
  }
}
