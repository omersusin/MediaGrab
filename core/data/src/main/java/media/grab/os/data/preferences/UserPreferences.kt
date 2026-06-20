package media.grab.os.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mediagrab_prefs")

class UserPreferences(private val context: Context) {

    private val keyTheme = stringPreferencesKey("theme")
    private val keyAccessMode = stringPreferencesKey("access_mode")
    private val keyOverlayEnabled = booleanPreferencesKey("overlay_enabled")

    val theme: Flow<String> = context.dataStore.data.map { it[keyTheme] ?: "system" }
    val accessMode: Flow<String> = context.dataStore.data.map { it[keyAccessMode] ?: "accessibility" }
    val overlayEnabled: Flow<Boolean> = context.dataStore.data.map { it[keyOverlayEnabled] ?: false }

    suspend fun setTheme(value: String) { context.dataStore.edit { it[keyTheme] = value } }
    suspend fun setAccessMode(value: String) { context.dataStore.edit { it[keyAccessMode] = value } }
    suspend fun setOverlayEnabled(value: Boolean) { context.dataStore.edit { it[keyOverlayEnabled] = value } }

    companion object {
        @Volatile private var instance: UserPreferences? = null
        fun getInstance(context: Context): UserPreferences {
            return instance ?: synchronized(this) {
                instance ?: UserPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
}
