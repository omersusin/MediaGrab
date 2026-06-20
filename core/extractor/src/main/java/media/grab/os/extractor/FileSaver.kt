package media.grab.os.extractor

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object FileSaver {
    private const val SUBDIR = "MediaGrab"

    fun save(
        context: Context,
        bytes: ByteArray,
        fileName: String,
        mimeType: String = "image/jpeg"
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveScoped(context, bytes, fileName, mimeType)
        } else {
            saveLegacy(bytes, fileName)
        }
    }

    private fun saveScoped(
        context: Context,
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ): String? {
        val collection = if (mimeType.startsWith("video/"))
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${'$'}{Environment.DIRECTORY_DOWNLOADS}/${'$'}SUBDIR")
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri = context.contentResolver.insert(collection, values) ?: return null
        context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }

        values.clear()
        values.put(MediaStore.MediaColumns.IS_PENDING, 0)
        context.contentResolver.update(uri, values, null, null)

        return uri.toString()
    }

    @Suppress("DEPRECATION")
    private fun saveLegacy(bytes: ByteArray, fileName: String): String? {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            SUBDIR
        )
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { it.write(bytes) }
        return file.absolutePath
    }
}
