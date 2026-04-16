package com.media.grab.grabber

import android.content.Context
import com.media.grab.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CacheScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val io: CoroutineDispatcher
) {
    private val mediaExtensions = listOf(".mp4", ".m4v", ".webm", ".mkv", ".3gp", ".mp3", ".m4a", ".aac", ".wav", ".ogg")

    suspend fun scanCache(): List<CachedMedia> = withContext(io) {
        val mediaList = mutableListOf<CachedMedia>()

        val cacheDirs = listOf(
            File(context.cacheDir, "video"),
            File(context.cacheDir, "media"),
            File(context.cacheDir, "temp"),
            File(context.externalCacheDir, "video"),
            File(context.externalCacheDir, "media")
        )

        cacheDirs.forEach { dir ->
            if (dir.exists()) {
                scanDirectory(dir, mediaList)
            }
        }

        mediaList.sortedByDescending { it.lastModified }
    }

    private fun scanDirectory(dir: File, list: MutableList<CachedMedia>) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                scanDirectory(file, list)
            } else if (isMediaFile(file)) {
                list.add(
                    CachedMedia(
                        path = file.absolutePath,
                        name = file.name,
                        size = file.length(),
                        lastModified = file.lastModified(),
                        mimeType = getMimeType(file)
                    )
                )
            }
        }
    }

    private fun isMediaFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        return mediaExtensions.any { ext == it.removePrefix(".") }
    }

    private fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "mp4", "m4v" -> "video/mp4"
            "webm" -> "video/webm"
            "mkv" -> "video/x-matroska"
            "3gp" -> "video/3gpp"
            "mp3" -> "audio/mpeg"
            "m4a", "aac" -> "audio/mp4"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            else -> "application/octet-stream"
        }
    }
}

data class CachedMedia(
    val path: String,
    val name: String,
    val size: Long,
    val lastModified: Long,
    val mimeType: String
)
