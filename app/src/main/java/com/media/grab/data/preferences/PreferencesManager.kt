package com.media.grab.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "media_grab_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val DOWNLOAD_PATH = stringPreferencesKey("download_path")
        val CONCURRENT_DOWNLOADS = intPreferencesKey("concurrent_downloads")
        val DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")
        val GRABBER_ENABLED = booleanPreferencesKey("grabber_enabled")
        val AUTO_GRAB = booleanPreferencesKey("auto_grab")
        val GRAB_NOTIFICATIONS = booleanPreferencesKey("grab_notifications")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
    }

    val downloadPath: Flow<String> = context.dataStore.data.map { it[DOWNLOAD_PATH] ?: defaultPath() }
    val concurrentDownloads: Flow<Int> = context.dataStore.data.map { it[CONCURRENT_DOWNLOADS] ?: 3 }
    val downloadQuality: Flow<String> = context.dataStore.data.map { it[DOWNLOAD_QUALITY] ?: "best" }
    val grabberEnabled: Flow<Boolean> = context.dataStore.data.map { it[GRABBER_ENABLED] ?: false }
    val autoGrab: Flow<Boolean> = context.dataStore.data.map { it[AUTO_GRAB] ?: false }
    val grabNotifications: Flow<Boolean> = context.dataStore.data.map { it[GRAB_NOTIFICATIONS] ?: true }
    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "system" }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val followSystemTheme: Flow<Boolean> = context.dataStore.data.map { it[FOLLOW_SYSTEM_THEME] ?: true }

    suspend fun setDownloadPath(path: String) { context.dataStore.edit { it[DOWNLOAD_PATH] = path } }
    suspend fun setConcurrentDownloads(n: Int) { context.dataStore.edit { it[CONCURRENT_DOWNLOADS] = n.coerceIn(1, 5) } }
    suspend fun setDownloadQuality(q: String) { context.dataStore.edit { it[DOWNLOAD_QUALITY] = q } }
    suspend fun setGrabberEnabled(e: Boolean) { context.dataStore.edit { it[GRABBER_ENABLED] = e } }
    suspend fun setAutoGrab(e: Boolean) { context.dataStore.edit { it[AUTO_GRAB] = e } }
    suspend fun setGrabNotifications(e: Boolean) { context.dataStore.edit { it[GRAB_NOTIFICATIONS] = e } }
    suspend fun setThemeMode(m: String) { context.dataStore.edit { it[THEME_MODE] = m } }
    suspend fun setDarkMode(e: Boolean) { context.dataStore.edit { it[DARK_MODE] = e } }
    suspend fun setFollowSystemTheme(e: Boolean) { context.dataStore.edit { it[FOLLOW_SYSTEM_THEME] = e } }

    private fun defaultPath(): String = "${context.getExternalFilesDir(null)?.absolutePath}/MediaGrab"
}
