package media.grab.os.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import media.grab.os.ui.home.HomeScreen
import media.grab.os.ui.downloads.DownloadsScreen
import media.grab.os.ui.paste.PasteUrlScreen
import media.grab.os.ui.settings.SettingsScreen
import media.grab.os.ui.onboarding.OnboardingScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DOWNLOADS = "downloads"
    const val PASTE = "paste"
    const val SETTINGS = "settings"
}

@Composable
fun MediaGrabNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.DOWNLOADS) { DownloadsScreen(navController) }
        composable(Routes.PASTE) { PasteUrlScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
    }
}
