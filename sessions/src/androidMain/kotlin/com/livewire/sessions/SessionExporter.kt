package com.livewire.sessions

import android.content.ClipData
import android.content.Context
import android.content.Intent
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal object SessionExporter {

  fun share(context: Context, session: RecordedSession) {
    val environment = sessionEnvironment() ?: return
    val sessionDir = File(environment.root.toString(), session.directoryName)
    if (!sessionDir.isDirectory) return

    val exportDir = SessionExportProvider.exportDir(context).apply { mkdirs() }
    val zipName = "livewire-session-${session.metadata.appId}-${session.directoryName}.zip"
    val zipFile = File(exportDir, zipName)

    ZipOutputStream(zipFile.outputStream().buffered()).use { zip ->
      sessionDir.walkTopDown().filter { it.isFile }.forEach { file ->
        zip.putNextEntry(ZipEntry("sessions/${session.directoryName}/${file.name}"))
        file.inputStream().use { it.copyTo(zip) }
        zip.closeEntry()
      }
    }

    val uri = SessionExportProvider.uriFor(context, zipName)
    val send = Intent(Intent.ACTION_SEND).apply {
      type = "application/zip"
      putExtra(Intent.EXTRA_STREAM, uri)
      clipData = ClipData.newRawUri(zipName, uri)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
      Intent.createChooser(send, "Share session")
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
    )
  }
}
