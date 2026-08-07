package com.livewire.sessions

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath

class SessionStoreTest {

  private val fileSystem = FileSystem.SYSTEM

  private fun tempRoot(): Path = Files.createTempDirectory("livewire-sessions").toOkioPath()

  private fun metadata(id: String, startedAt: Long) = SessionMetadata(
    id = id,
    startedAt = startedAt,
    appId = "com.example.app",
    platform = "test",
    osVersion = "Test 1",
  )

  private fun writeSession(root: Path, metadata: SessionMetadata, channels: Map<String, List<String>>) {
    val dir = root / metadata.id
    fileSystem.createDirectories(dir)
    fileSystem.write(dir / SessionStore.MetadataFileName) {
      writeUtf8(SessionStore.json.encodeToString(SessionMetadata.serializer(), metadata))
    }
    channels.forEach { (fileName, lines) ->
      fileSystem.write(dir / fileName) {
        lines.forEach { line ->
          writeUtf8(line)
          writeUtf8("\n")
        }
      }
    }
  }

  @Test
  fun listsSessionsNewestFirst() {
    val root = tempRoot()
    writeSession(root, metadata("session-100", 100), mapOf("logs.jsonl" to listOf("{}")))
    writeSession(root, metadata("session-200", 200), mapOf("logs.jsonl" to listOf("{}", "{}")))

    val sessions = SessionStore(fileSystem, root).sessions()

    assertEquals(listOf("session-200", "session-100"), sessions.map { it.metadata.id })
  }

  @Test
  fun readsChannelLinesSkippingBlanks() {
    val root = tempRoot()
    writeSession(root, metadata("session-100", 100), mapOf("logs.jsonl" to listOf("""{"a":1}""", "", """{"b":2}""")))

    val store = SessionStore(fileSystem, root)
    val session = store.sessions().single()

    assertEquals(listOf("""{"a":1}""", """{"b":2}"""), store.lines(session, "logs.jsonl"))
    assertEquals(emptyList(), store.lines(session, "network.jsonl"))
  }

  @Test
  fun countsEntriesPerChannel() {
    val root = tempRoot()
    writeSession(
      root,
      metadata("session-100", 100),
      mapOf(
        "logs.jsonl" to listOf("{}", "{}", "{}"),
        "network.jsonl" to listOf("{}"),
      ),
    )

    val store = SessionStore(fileSystem, root)
    val counts = store.channelCounts(store.sessions().single())

    assertEquals(mapOf("logs" to 3, "network" to 1), counts)
  }

  @Test
  fun ignoresDirectoriesWithoutMetadata() {
    val root = tempRoot()
    writeSession(root, metadata("session-100", 100), emptyMap())
    fileSystem.createDirectories(root / "session-broken")

    assertEquals(1, SessionStore(fileSystem, root).sessions().size)
  }
}
