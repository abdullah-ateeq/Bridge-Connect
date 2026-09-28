package com.example.bridgeconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bridgeconnect.ui.screens.AboutScreen
import com.example.bridgeconnect.ui.screens.GameScreen
import com.example.bridgeconnect.ui.screens.HomeScreen
import com.example.bridgeconnect.ui.screens.HowToPlayScreen
import com.example.bridgeconnect.ui.screens.ResultScreen
import com.example.bridgeconnect.ui.screens.SettingsScreen
import com.example.bridgeconnect.ui.screens.SplashScreen
import com.example.bridgeconnect.viewmodel.GameViewModel
import com.example.bridgeconnect.viewmodel.SettingsViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val gameState by gameViewModel.state.collectAsState()
    val settings by settingsViewModel.settings.collectAsState()
    val statistics by settingsViewModel.statistics.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                statistics = statistics,
                onPlayClicked = {
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClicked = {
                    navController.navigate(Screen.HowToPlay.route)
                },
                onSettingsClicked = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClicked = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                state = gameState,
                settings = settings,
                onNodeClicked = { node ->
                    gameViewModel.placeNode(node)
                },
                onUndoClicked = {
                    gameViewModel.undo()
                },
                onRestartConfirmed = {
                    gameViewModel.restart()
                },
                onHomeClicked = {
                    navController.popBackStack(Screen.Home.route, false)
                },
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                state = gameState,
                onPlayAgain = {
                    gameViewModel.playAgain()
                    navController.popBackStack(Screen.Game.route, false)
                },
                onHomeClicked = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }

        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onThemeModeChanged = { settingsViewModel.setThemeMode(it) },
                onBoardThemeChanged = { settingsViewModel.setBoardTheme(it) },
                onSoundChanged = { settingsViewModel.setSoundEnabled(it) },
                onHapticsChanged = { settingsViewModel.setHapticsEnabled(it) },
                onShowLastMoveChanged = { settingsViewModel.setShowLastMove(it) },
                onHighlightTargetSidesChanged = { settingsViewModel.setHighlightTargetSides(it) },
                onResetStatsConfirmed = { settingsViewModel.resetStatistics() },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}
