package media.grab.os.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import media.grab.os.ui.navigation.MediaGrabNavHost
import media.grab.os.ui.theme.MediaGrabTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaGrabApp()
        }
    }
}

@Composable
private fun MediaGrabApp() {
    MediaGrabTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MediaGrabNavHost()
        }
    }
}
