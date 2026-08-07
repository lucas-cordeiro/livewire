package com.livewire.sessions

import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path

class SessionStore(
  private val fileSystem: FileSystem,
  private val root: Path,
) {

  fun sessions(): List<RecordedSession> {
    val directories = runCatching { fileSystem.list(root) }.getOrDefault(emptyList())
      .filter { runCatching { fileSystem.metadata(it).isDirectory }.getOrDefault(false) }

    return directories.mapNotNull { directory ->
      val metadata = runCatching {
        fileSystem.read(directory / MetadataFileName) { readUtf8() }
          .let { json.decodeFromString(SessionMetadata.serializer(), it) }
      }.getOrNull() ?: return@mapNotNull null

      RecordedSession(
        metadata = metadata,
        directoryName = directory.name,
      )
    }.sortedByDescending { it.metadata.startedAt }
  }

  fun lines(session: RecordedSession, fileName: String): List<String> {
    return runCatching {
      fileSystem.read(root / session.directoryName / fileName) { readUtf8() }
        .lineSequence()
        .filter { it.isNotBlank() }
        .toList()
    }.getOrDefault(emptyList())
  }

  fun channelCounts(session: RecordedSession): Map<String, Int> {
    val directory = root / session.directoryName
    return runCatching { fileSystem.list(directory) }.getOrDefault(emptyList())
      .filter { it.name.endsWith(".jsonl") }
      .associate { file ->
        val count = runCatching {
          fileSystem.read(file) { readUtf8() }.lineSequence().count { it.isNotBlank() }
        }.getOrDefault(0)
        file.name.removeSuffix(".jsonl") to count
      }
  }

  companion object {
    const val MetadataFileName = "metadata.json"

    val json = Json {
      ignoreUnknownKeys = true
    }
  }
}
