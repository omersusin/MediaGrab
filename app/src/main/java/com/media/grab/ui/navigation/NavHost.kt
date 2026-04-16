package com.media.grab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.media.grab.ui.screens.grabber.GrabberScreen
import com.media.grab.ui.screens.history.HistoryScreen
import com.media.grab.ui.screens.home.HomeScreen
import com.media.grab.ui.screens.settings.SettingsScreen

@Composable
fun MediaGrabNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Grabber.route) { GrabberScreen() }
        composable(Screen.History.route) { HistoryScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
