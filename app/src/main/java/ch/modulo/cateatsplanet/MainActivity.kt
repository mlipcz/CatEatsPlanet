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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
                    var catX by remember { mutableStateOf(500f) }
                    var catY by remember { mutableStateOf(500f) }
                    val focusRequester = remember { FocusRequester() }

                    NightSkyBackground(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .focusable()
                            .onKeyEvent {
                                if (it.type == KeyEventType.KeyDown) {
                                    val step = 30f
                                    when (it.key) {
                                        Key.DirectionUp -> catY -= step
                                        Key.DirectionDown -> catY += step
                                        Key.DirectionLeft -> catX -= step
                                        Key.DirectionRight -> catX += step
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
                                    .size(100.dp)
                            )
                            
                            Greeting(
                                name = "kot",
                                modifier = Modifier.align(Alignment.BottomStart)
                            )
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
fun CatSprite(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val catColor = Color(0xFF888888) // Gray cat

        // Ears
        val leftEar = Path().apply {
            moveTo(w * 0.25f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.15f)
            lineTo(w * 0.45f, h * 0.35f)
            close()
        }
        drawPath(leftEar, catColor)

        val rightEar = Path().apply {
            moveTo(w * 0.55f, h * 0.35f)
            lineTo(w * 0.65f, h * 0.15f)
            lineTo(w * 0.75f, h * 0.35f)
            close()
        }
        drawPath(rightEar, catColor)

        // Head
        drawOval(
            color = catColor,
            topLeft = Offset(w * 0.25f, h * 0.3f),
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.4f)
        )

        // Eyes
        drawCircle(
            color = Color.Yellow,
            radius = w * 0.06f,
            center = Offset(w * 0.4f, h * 0.45f)
        )
        drawCircle(
            color = Color.Yellow,
            radius = w * 0.06f,
            center = Offset(w * 0.6f, h * 0.45f)
        )

        // Pupils
        drawCircle(
            color = Color.Black,
            radius = w * 0.02f,
            center = Offset(w * 0.4f, h * 0.45f)
        )
        drawCircle(
            color = Color.Black,
            radius = w * 0.02f,
            center = Offset(w * 0.6f, h * 0.45f)
        )

        // Nose
        val nosePath = Path().apply {
            moveTo(w * 0.47f, h * 0.55f)
            lineTo(w * 0.53f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.58f)
            close()
        }
        drawPath(nosePath, Color(0xFFFFC0CB))
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
