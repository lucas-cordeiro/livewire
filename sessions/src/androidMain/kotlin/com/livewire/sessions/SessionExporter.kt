package com.livewire.sessions

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

internal object SessionExporter {

  fun share(context: Context, session: RecordedSession) {
    val zipFile = createZip(context, session) ?: return

    val uri = SessionExportProvider.uriFor(context, zipFile.name)
    val send = Intent(Intent.ACTION_SEND).apply {
      type = "application/zip"
      putExtra(Intent.EXTRA_STREAM, uri)
      clipData = ClipData.newRawUri(zipFile.name, uri)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
      Intent.createChooser(send, "Share session")
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
    )
  }

  fun download(context: Context, session: RecordedSession): String? {
    val zipFile = createZip(context, session) ?: return null

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      downloadViaMediaStore(context, zipFile)
    } else {
      downloadToAppExternalDir(context, zipFile)
    }
  }

  private fun downloadViaMediaStore(context: Context, zipFile: File): String? {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, zipFile.name)
      put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
      put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
      put(MediaStore.MediaColumns.IS_PENDING, 1)
    }

    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
    val copied = runCatching {
      resolver.openOutputStream(uri)?.use { output ->
        zipFile.inputStream().use { it.copyTo(output) }
      } ?: error("no output stream")
    }.isSuccess

    if (!copied) {
      resolver.delete(uri, null, null)
      return null
    }

    values.clear()
    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
    resolver.update(uri, values, null, null)

    val name = resolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
      ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
      ?: zipFile.name
    return "${Environment.DIRECTORY_DOWNLOADS}/$name"
  }

  private fun downloadToAppExternalDir(context: Context, zipFile: File): String? {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return null
    val target = File(dir, zipFile.name)
    return runCatching {
      zipFile.copyTo(target, overwrite = true)
      target.absolutePath
    }.getOrNull()
  }

  private fun createZip(context: Context, session: RecordedSession): File? {
    val environment = sessionEnvironment() ?: return null
    val sessionDir = File(environment.root.toString(), session.directoryName)
    if (!sessionDir.isDirectory) return null

    val exportDir = SessionExportProvider.exportDir(context).apply { mkdirs() }
    val zipFile = File(exportDir, zipFileName(session))

    ZipOutputStream(zipFile.outputStream().buffered()).use { zip ->
      sessionDir.walkTopDown().filter { it.isFile }.forEach { file ->
        zip.putNextEntry(ZipEntry("sessions/${session.directoryName}/${file.name}"))
        file.inputStream().use { it.copyTo(zip) }
        zip.closeEntry()
      }
    }
    return zipFile
  }

  private fun zipFileName(session: RecordedSession): String {
    val startedAt = Instant.ofEpochMilli(session.metadata.startedAt)
      .atZone(ZoneId.systemDefault())
      .format(zipNameFormatter)
    return "livewire-${session.metadata.appId}-$startedAt.zip"
  }

  private val zipNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
}
