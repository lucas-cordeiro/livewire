package com.livewire.sessions

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File
import java.io.FileNotFoundException

class SessionExportProvider : ContentProvider() {

  override fun onCreate(): Boolean = true

  override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
    return ParcelFileDescriptor.open(resolveFile(uri), ParcelFileDescriptor.MODE_READ_ONLY)
  }

  override fun query(
    uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?,
  ): Cursor {
    val file = resolveFile(uri)
    val columns = projection ?: arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
    val cursor = MatrixCursor(columns, 1)
    cursor.addRow(
      columns.map { column ->
        when (column) {
          OpenableColumns.DISPLAY_NAME -> file.name
          OpenableColumns.SIZE -> file.length()
          else -> null
        }
      },
    )
    return cursor
  }

  override fun getType(uri: Uri): String = "application/zip"

  override fun insert(uri: Uri, values: ContentValues?): Uri? = null

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<String>?,
  ): Int = 0

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

  private fun resolveFile(uri: Uri): File {
    val name = uri.lastPathSegment ?: throw FileNotFoundException(uri.toString())
    if (name.contains('/') || name.contains("..")) throw FileNotFoundException(uri.toString())
    val context = context ?: throw FileNotFoundException(uri.toString())
    val file = File(exportDir(context), name)
    if (!file.isFile) throw FileNotFoundException(uri.toString())
    return file
  }

  companion object {
    fun exportDir(context: Context): File = File(context.cacheDir, "livewire/session-exports")

    fun uriFor(context: Context, fileName: String): Uri =
      Uri.parse("content://${context.packageName}.livewire.sessions.exports/$fileName")
  }
}
