package yt.tjd.composegame.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlin.math.ceil
import kotlin.random.Random

fun NavGraphBuilder.addGame(onGameEnd: () -> Unit) {
  composable(
    route = "balldefender/game?numPlayers={numPlayers}",
    arguments = listOf(navArgument("numPlayers") { type = NavType.IntType; defaultValue = 1 })
  ) {
    val numPlayers by remember { derivedStateOf { it.arguments?.getInt("numPlayers") ?: 0 } }

    GameScreen(numPlayers = numPlayers, onGameEnd = onGameEnd)
  }
}

fun NavController.navigateToGame(numPlayers: Int) {
  navigate("balldefender/game?numPlayers=$numPlayers")
}

@Composable
fun GameScreen(numPlayers: Int, onGameEnd: () -> Unit) {
  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      val winningScore = 5
      var gameInProgress by rememberSaveable { mutableStateOf(false) }
      var user1Lost by rememberSaveable { mutableStateOf(false) }
      var user2Lost by rememberSaveable { mutableStateOf(false) }
      val aiEnabled by remember { derivedStateOf { numPlayers == 1 } }

      val uiColor = MaterialTheme.colorScheme.onSurfaceVariant

      var user1Points by rememberSaveable { mutableStateOf(0) }
      var user2Points by rememberSaveable { mutableStateOf(0) }

      Game(
        aiEnabled = aiEnabled,
        gameInProgress = gameInProgress,
        user1Lost = {
          gameInProgress = false
          user1Lost = true
          user2Points += 1
        },
        user2Lost = {
          gameInProgress = false
          user2Lost = true
          user1Points += 1
        },
        modifier = Modifier.fillMaxSize()
      )



      Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
          color = uiColor,
          start = Offset(x = 0f, y = center.y),
          end = Offset(x = size.width, y = center.y),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f))
        )
      }

      Column(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(20.dp)
      ) {
        Text(text = user2Points.toString(), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = user1Points.toString(), style = MaterialTheme.typography.headlineMedium)
      }

      if (!gameInProgress && (user1Lost || user2Lost)) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              color = Color.Black.copy(alpha = .5f)
            ),
          contentAlignment = Alignment.Center
        ) {
          Surface(
            shape = MaterialTheme.shapes.medium, modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = when {
                  aiEnabled && user1Lost && user2Points < winningScore -> "They Get a point"
                  aiEnabled && user1Lost && user2Points == winningScore -> "You LOST. Looser."
                  aiEnabled && user2Lost && user1Points < winningScore -> "You get a point"
                  aiEnabled && user2Lost && user1Points == winningScore -> "You WON. YAY."
                  user1Lost && user2Points < winningScore -> "Player 2 gets a point!"
                  user1Lost && user2Points == winningScore -> "Player 2 WINS"
                  user2Lost && user1Points < winningScore -> "Player 1 gets a point!"
                  else -> "Player 1 WINS"
                },
                style = MaterialTheme.typography.headlineMedium
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(text = "You can play again tho")

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
                modifier = Modifier.fillMaxWidth()
              ) {
                TextButton(
                  onClick = {
                    user1Lost = false
                    user2Lost = false
                    if (user1Points == winningScore || user2Points == winningScore) {
                      user1Points = 0
                      user2Points = 0
                    }
                  }
                ) {
                  Text(text = if (user1Points < winningScore && user2Points < winningScore) "Aight" else "Play Again")
                }

                if (user1Points == winningScore || user2Points == winningScore) {
                  TextButton(
                    onClick = onGameEnd
                  ) {
                    Text(text = "Main Menu")
                  }
                }
              }
            }
          }
        }
      } else if (!gameInProgress) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = .4f)),
          contentAlignment = Alignment.Center
        ) {

          var targetValue by remember { mutableStateOf(5f) }

          val countDown by animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(5000, easing = LinearEasing),
            finishedListener = {
              gameInProgress = true
            }
          )

          LaunchedEffect(gameInProgress) {
            targetValue = 0f
          }

          val text by remember(countDown) {
            derivedStateOf {
              val wholeNumber = ceil(countDown) - 2
              if (wholeNumber > 0) wholeNumber.toInt().toString()
              else "GO!"
            }
          }

          Text(text = text, style = MaterialTheme.typography.displayLarge, color = Color.Magenta)

        }
      }
    }
  }
}


@Composable
fun Game(
  aiEnabled: Boolean,
  gameInProgress: Boolean,
  user1Lost: () -> Unit,
  user2Lost: () -> Unit,
  modifier: Modifier = Modifier
) {

  val density = LocalDensity.current

  BoxWithConstraints(
    modifier = modifier
  ) {
    val ballRadius = 10.dp
    val visiblePaddleWidth = 80.dp
    val visiblePaddleHeight = 20.dp
    val paddleVerticalOffset = 20.dp
    val maxBallXVelocity = 2f
    val maxBallYVelocity = 3f
    var topPaddleHorizontalOffset by remember(gameInProgress) { mutableStateOf((maxWidth / 2) - (visiblePaddleWidth / 2)) }
    var bottomPaddleHorizontalOffset by remember(gameInProgress) { mutableStateOf((maxWidth / 2) - (visiblePaddleWidth / 2)) }

    var xVelocity by remember(gameInProgress) { mutableStateOf(Random.nextInt(-maxBallXVelocity.toInt(), maxBallXVelocity.toInt()).toFloat()) }
    var yVelocity by remember(gameInProgress) { mutableStateOf(-maxBallYVelocity) }

    var xPosBall by remember(gameInProgress) { mutableStateOf(maxWidth / 2) }
    var yPosBall by remember(gameInProgress) { mutableStateOf(maxHeight / 2) }

    LaunchedEffect(xVelocity, yVelocity, gameInProgress, maxWidth) {

      val aiVelocity = maxBallXVelocity / 2f
      var lastCalcTime = withFrameMillis { it }
      while (gameInProgress) {
        withFrameMillis {
          val deltaTime = it - lastCalcTime

          xPosBall += with(density) { (xVelocity * deltaTime).toDp() }
          yPosBall += with(density) { (yVelocity * deltaTime).toDp() }

          if (aiEnabled) {
            topPaddleHorizontalOffset += with(density) { ((if (xPosBall > topPaddleHorizontalOffset + (visiblePaddleWidth / 2)) aiVelocity else -aiVelocity) * deltaTime).toDp() }
          }

          if ( // top player
            yPosBall <= paddleVerticalOffset + visiblePaddleHeight // Correct y
            && (xPosBall >= topPaddleHorizontalOffset - (ballRadius * 2) && xPosBall <= topPaddleHorizontalOffset + visiblePaddleWidth) // correct x
            && yVelocity < 0 // correct direction / only do it once
          ) {
            yVelocity *= -1
            val edgeOfPaddle = topPaddleHorizontalOffset
            val centerOfPaddle = edgeOfPaddle + (visiblePaddleWidth / 2)
            val centerOfBall = xPosBall + ballRadius
            val ballCenterPosRelativeToCenterOfPaddle = centerOfBall - centerOfPaddle
            val ballHeightOnPaddleRatio = ballCenterPosRelativeToCenterOfPaddle / ((visiblePaddleWidth / 2) + (ballRadius * 2))
            xVelocity = (ballHeightOnPaddleRatio * maxBallXVelocity).coerceIn(-maxBallXVelocity, maxBallXVelocity)
          } else if ( // Bottom player
            yPosBall >= maxHeight - paddleVerticalOffset - visiblePaddleHeight - (ballRadius * 2) // Correct y
            && (xPosBall >= bottomPaddleHorizontalOffset - (ballRadius * 2) && xPosBall <= bottomPaddleHorizontalOffset + visiblePaddleWidth) // correct x
            && yVelocity > 0 // correct direction / only do it once
          ) {
            yVelocity *= -1
            val edgeOfPaddle = bottomPaddleHorizontalOffset
            val centerOfPaddle = edgeOfPaddle + (visiblePaddleWidth / 2)
            val centerOfBall = xPosBall + ballRadius
            val ballCenterPosRelativeToCenterOfPaddle = centerOfBall - centerOfPaddle
            val ballHeightOnPaddleRatio = ballCenterPosRelativeToCenterOfPaddle / ((visiblePaddleWidth / 2) + (ballRadius * 2))
            xVelocity = (ballHeightOnPaddleRatio * maxBallXVelocity).coerceIn(-maxBallXVelocity, maxBallXVelocity)
          } else if (yPosBall >= maxHeight - (ballRadius * 2) && yVelocity > 0) {
            user1Lost()
          } else if (yPosBall <= 0.dp && yVelocity < 0) {
            user2Lost()
          }

          if (xPosBall >= maxWidth - (ballRadius * 2) && xVelocity > 0) {
            xVelocity *= -1f
          } else if (xPosBall <= 0.dp && xVelocity < 0) {
            xVelocity *= -1f
          }

          lastCalcTime = it
        }
      }
    }

    Box(
      modifier = Modifier
        .offset(bottomPaddleHorizontalOffset, maxHeight - paddleVerticalOffset - visiblePaddleHeight)
        .draggable(rememberDraggableState { bottomPaddleHorizontalOffset += with(density) { it.toDp() } }, orientation = Orientation.Horizontal)
        .size(visiblePaddleWidth, visiblePaddleHeight)
        .background(color = Color.Red, shape = CircleShape)
    )

    Box(
      modifier = Modifier
        .offset(topPaddleHorizontalOffset, paddleVerticalOffset)
        .then(
          if (!aiEnabled) Modifier.draggable(rememberDraggableState { topPaddleHorizontalOffset += with(density) { it.toDp() } }, orientation = Orientation.Horizontal)
          else Modifier
        )
        .size(visiblePaddleWidth, visiblePaddleHeight)
        .background(color = Color.Blue, shape = CircleShape)
    )

    Box(
      modifier = Modifier
        .offset(xPosBall, yPosBall)
        .size(ballRadius * 2)
        .background(color = Color.White, shape = CircleShape)
    )

    // Controls for the paddles come through here. The user can drag anywhere on their side of the court
    Column(modifier.fillMaxSize()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .then(
            if (!aiEnabled) Modifier.draggable(rememberDraggableState { topPaddleHorizontalOffset += with(density) { it.toDp() } }, orientation = Orientation.Horizontal)
            else Modifier
          )
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .draggable(
            rememberDraggableState { bottomPaddleHorizontalOffset += with(density) { it.toDp() } }, orientation = Orientation.Horizontal
          )
      )
    }
  }
}
