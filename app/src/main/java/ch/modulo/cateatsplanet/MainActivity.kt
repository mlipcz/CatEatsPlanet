package ch.modulo.cateatsplanet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import ch.modulo.cateatsplanet.ui.theme.CatEatsPlanetTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatEatsPlanetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    var catX by remember { mutableFloatStateOf(500f) }
                    var catY by remember { mutableFloatStateOf(500f) }
                    val focusRequester = remember { FocusRequester() }
                    val density = LocalDensity.current
                    val catSizeDp = 150.dp
                    val catSizePx = with(density) { catSizeDp.toPx() }

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val maxWidthPx = constraints.maxWidth.toFloat()
                        val maxHeightPx = constraints.maxHeight.toFloat()

                        NightSkyBackground(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .focusable()
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown) {
                                        val step = 30f
                                        when (it.key) {
                                            Key.DirectionUp -> catY = (catY - step).coerceAtLeast(0f)
                                            Key.DirectionDown -> catY =
                                                (catY + step).coerceAtMost(maxHeightPx - catSizePx)
                                            Key.DirectionLeft -> catX =
                                                (catX - step).coerceAtLeast(0f)
                                            Key.DirectionRight -> catX =
                                                (catX + step).coerceAtMost(maxWidthPx - catSizePx)
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CatSprite(
                                    modifier = Modifier
                                        .offset { IntOffset(catX.toInt(), catY.toInt()) }
                                        .size(catSizeDp)
                                )

                                Greeting(
                                    name = "kot",
                                    modifier = Modifier.align(Alignment.BottomStart)
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
        color = Color.White
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
