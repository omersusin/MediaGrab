package media.grab.os.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import media.grab.os.ui.downloads.DownloadsScreen
import media.grab.os.ui.home.HomeScreen
import media.grab.os.ui.onboarding.OnboardingScreen
import media.grab.os.ui.paste.PasteUrlScreen
import media.grab.os.ui.settings.SettingsScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DOWNLOADS = "downloads"
    const val PASTE = "paste"
    const val SETTINGS = "settings"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaGrabNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }
        composable(Routes.HOME) {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) { padding ->
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
                    HomeScreen(navController)
                }
            }
        }
        composable(Routes.DOWNLOADS) {
            Scaffold(
                bottomBar = { BottomBar(navController) }
            ) { padding ->
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
                    DownloadsScreen(navController)
                }
            }
        }
        composable(Routes.PASTE) { PasteUrlScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    var selected by remember { mutableStateOf(0) }
    NavigationBar {
        NavigationBarItem(
            selected = selected == 0,
            onClick = { selected = 0; navController.navigate(Routes.HOME) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = { selected = 1; navController.navigate(Routes.DOWNLOADS) },
            icon = { Icon(Icons.Default.Download, null) },
            label = { Text("Downloads") }
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = { selected = 2; navController.navigate(Routes.PASTE) },
            icon = { Icon(Icons.Default.ContentPaste, null) },
            label = { Text("Paste") }
        )
        NavigationBarItem(
            selected = selected == 3,
            onClick = { selected = 3; navController.navigate(Routes.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text("Settings") }
        )
    }
}
