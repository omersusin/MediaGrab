package media.grab.os

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import media.grab.os.data.preferences.UserPreferences
import media.grab.os.ui.navigation.MediaGrabNavHost
import media.grab.os.ui.theme.MediaGrabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = UserPreferences.getInstance(applicationContext)
        setContent {
            val themePref by prefs.theme.collectAsState(initial = "system")
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themePref) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }
            MediaGrabTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                MediaGrabNavHost(navController)
            }
        }
    }
}
