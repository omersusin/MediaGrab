package com.media.grab.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.media.grab.data.local.entity.DownloadEntity
import com.media.grab.data.local.entity.DownloadStatus
import com.media.grab.grabber.MediaDetector
import com.media.grab.ui.theme.Facebook
import com.media.grab.ui.theme.Instagram
import com.media.grab.ui.theme.Snapchat
import com.media.grab.ui.theme.TikTok
import com.media.grab.ui.theme.Twitter
import com.media.grab.ui.theme.YouTube

@Composable
fun DownloadCard(
    download: DownloadEntity,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val platform = MediaDetector.detectPlatform(download.url)
    val platformColor = getPlatformColor(platform.name)
    val progress by animateFloatAsState(
        targetValue = if (download.fileSize > 0) download.downloadedSize.toFloat() / download.fileSize else 0f,
        label = "progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Platform indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(platformColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPlatformIcon(platform.name),
                        contentDescription = platform.name,
                        tint = platformColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = download.title.ifBlank { platform.name },
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = platform.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = download.status)
            }

            if (download.status == DownloadStatus.DOWNLOADING) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(progress * 100).toInt()}% - ${formatFileSize(download.downloadedSize)} / ${formatFileSize(download.fileSize)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (download.status) {
                    DownloadStatus.DOWNLOADING -> {
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.Cancel, "Cancel", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    DownloadStatus.FAILED -> {
                        IconButton(onClick = onRetry) {
                            Icon(Icons.Default.Refresh, "Retry")
                        }
                    }
                    DownloadStatus.COMPLETED -> {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Download, "Open")
                        }
                    }
                    else -> {}
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: DownloadStatus) {
    val (color, text) = when (status) {
        DownloadStatus.PENDING -> MaterialTheme.colorScheme.outline to "Pending"
        DownloadStatus.QUEUED -> MaterialTheme.colorScheme.secondary to "Queued"
        DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.primary to "Downloading"
        DownloadStatus.PAUSED -> MaterialTheme.colorScheme.tertiary to "Paused"
        DownloadStatus.COMPLETED -> Color(0xFF4CAF50) to "Done"
        DownloadStatus.FAILED -> MaterialTheme.colorScheme.error to "Failed"
        DownloadStatus.CANCELLED -> MaterialTheme.colorScheme.outline to "Cancelled"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun getPlatformIcon(name: String) = when (name) {
    "YouTube" -> Icons.Default.PlayArrow
    "Instagram" -> Icons.Default.PlayArrow
    "TikTok" -> Icons.Default.PlayArrow
    else -> Icons.Default.Download
}

private fun getPlatformColor(name: String) = when (name) {
    "YouTube" -> YouTube
    "TikTok" -> TikTok
    "Instagram" -> Instagram
    "Facebook" -> Facebook
    "Twitter" -> Twitter
    "Snapchat" -> Snapchat
    else -> Color.Gray
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    return "%.1f %s".format(size, units[unitIndex])
}
