package yt.tjd.composegame.landing

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.addMainMenu(startGame: (numPlayers: Int) -> Unit) {
  composable(
    route = "balldefender/main",
  ) {
    MainMenu(startGame = startGame)
  }
}

@Composable
fun MainMenu(startGame: (numPlayers: Int) -> Unit) {
  Surface(color = Color.Black) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

      val infiniteRepeatable = rememberInfiniteTransition(label = "infinite")

      val animation by infiniteRepeatable.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
          animation = tween(10000, easing = LinearEasing)
        ),
        label = "angle"
      )

      val density = LocalDensity.current

      val transition = rememberInfiniteTransition("transition")

      val offsetX by transition.animateFloat(
        initialValue = 0f,
        targetValue = with(density) { (maxWidth - 20.dp).toPx() },
        animationSpec = infiniteRepeatable(
          tween(3000, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
          initialStartOffset = StartOffset(1500, offsetType = StartOffsetType.FastForward)
        ),
        label = "offsetX"
      )

      val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = with(density) { (maxHeight - 20.dp).toPx() },
        animationSpec = infiniteRepeatable(
          tween(3000, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
      )

      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .offset(
            x = with(density) { offsetX.toDp() },
            y = with(density) { offsetY.toDp() }
          )
          .size(20.dp)
          .background(color = Color.White.copy(alpha = 0.5f), shape = CircleShape)
      )

      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .rotate(animation)
      ) {
        drawCircle(
          brush = Brush.linearGradient(listOf(Color.Red.copy(alpha = .2f), Color.Blue.copy(alpha = .2f))),
          radius = (size.height / 2f) * 1.25f
        )
      }

      Column(modifier = Modifier.width(IntrinsicSize.Min)) {
        OutlinedButton(onClick = { startGame(1) }) {
          Text(text = "SINGLE PLAYER", maxLines = 1, modifier = Modifier.width(IntrinsicSize.Max))
        }

        OutlinedButton(onClick = { startGame(2) }, modifier = Modifier.fillMaxWidth()) {
          Text(text = "PVP")
        }
      }
    }
  }
}