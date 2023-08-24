package yt.tjd.composegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import yt.tjd.composegame.game.addGame
import yt.tjd.composegame.game.navigateToGame
import yt.tjd.composegame.landing.addMainMenu
import yt.tjd.composegame.ui.theme.ComposeGameTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      ComposeGameTheme(darkTheme = true) {

        val systemUiController = rememberSystemUiController()
        systemUiController.isSystemBarsVisible = false

        // A surface container using the 'background' color from the theme

        val navController = rememberNavController()

        NavHost(
          navController = navController,
          route = "balldefender",
          startDestination = "balldefender/main"
        ) {
          addMainMenu(startGame = navController::navigateToGame)

          addGame(onGameEnd = navController::navigateUp)
        }
      }
    }
  }
}