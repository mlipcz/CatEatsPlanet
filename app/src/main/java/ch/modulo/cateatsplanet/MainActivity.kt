package ch.modulo.cateatsplanet

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import ch.modulo.cateatsplanet.ui.theme.CatEatsPlanetTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedBoxWithConstraintsScope")
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatEatsPlanetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    val catController = rememberCatController()
                    val focusRequester = remember { FocusRequester() }
                    val density = LocalDensity.current
                    val catSizeDp = 150.dp
                    val catSizePx = with(density) { catSizeDp.toPx() }

                    var timeLeft by remember { mutableIntStateOf(60) }

                    LaunchedEffect(key1 = timeLeft) {
                        if (timeLeft > 0) {
                            delay(1000L)
                            timeLeft--
                        }
                    }

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val maxWidthPx = constraints.maxWidth.toFloat()
                        val maxHeightPx = constraints.maxHeight.toFloat()

                        val infiniteTransition = rememberInfiniteTransition(label = "pupilTransition")
                        val pupilRadiusFactor by infiniteTransition.animateFloat(
                            initialValue = 0.01f,
                            targetValue = 0.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pupilRadius"
                        )
                        val whiskersFactor by infiniteTransition.animateFloat(
                            initialValue = 0.01f,
                            targetValue = 0.07f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1237),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "whiskersAngle"
                        )

                        NightSkyBackground(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .focusable()
                                .onKeyEvent {
                                    catController.handleKeyEvent(it, maxWidthPx, maxHeightPx, catSizePx)
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CatSprite(
                                    modifier = Modifier
                                        .offset { IntOffset(catController.x.toInt(), catController.y.toInt()) }
                                        .size(catSizeDp),
                                    pupilRadiusFactor = pupilRadiusFactor,
                                    whiskersFactor = whiskersFactor
                                )

                                Greeting(
                                    name = "kot",
                                    modifier = Modifier.align(Alignment.BottomStart).padding(32.dp)
                                )

                                Text(
                                    text = if (timeLeft > 0) timeLeft.toString() else "Next level",
                                    color = Color.Cyan,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(32.dp)
                                )
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            }
        }
    }
}

@Composable
fun NightSkyBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val skyColors = listOf(
        Color(0xFF020111), // Very dark blue/black
        Color(0xFF020111),
        Color(0xFF050a30), // Deep blue
        Color(0xFF000c40)  // Midnight blue
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(skyColors))
    ) {
        val starCount = 300
        val stars = remember {
            List(starCount) {
                Offset(Random.nextFloat(), Random.nextFloat()) to Random.nextFloat()
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { (position, alpha) ->
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = 2f,
                    center = Offset(position.x * size.width, position.y * size.height)
                )
            }
        }
        content()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Kiedy $name nadchodzi, co zjada planety",
        modifier = modifier,
        color = Color.Cyan
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CatEatsPlanetTheme {
        NightSkyBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                CatSprite(
                    modifier = Modifier
                        .offset(100.dp, 100.dp)
                        .size(100.dp)
                )
                Greeting("kot")
            }
        }
    }
}
