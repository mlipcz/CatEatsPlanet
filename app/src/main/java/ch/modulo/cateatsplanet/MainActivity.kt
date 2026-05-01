package ch.modulo.cateatsplanet

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.SoundPool
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import ch.modulo.cateatsplanet.sprites.CatSprite
import ch.modulo.cateatsplanet.sprites.PlanetSprite
import ch.modulo.cateatsplanet.ui.theme.CatEatsPlanetTheme
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.random.Random

data class PlanetData(
    val name: String,
    val color: Color,
    val pos: Offset,
    var isEaten: Boolean = false
)

data class GameContext(
    val planets: SnapshotStateList<PlanetData>,
    val catController: CatController,
    val timeLeft: Int,
    val maxWidth: Float,
    val maxHeight: Float,
    val catSizePx: Float,
    val catSizeDp: androidx.compose.ui.unit.Dp,
    val pupilRadius: Float,
    val whiskersAngle: Float
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatEatsPlanetTheme {
                Surface(modifier = Modifier.fillMaxSize(), shape = RectangleShape) {
                    CatEatsPlanetGame()
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CatEatsPlanetGame() {
    val catController = rememberCatController()
    val focusRequester = remember { FocusRequester() }
    val catSizeDp = 150.dp
    val catSizePx = with(LocalDensity.current) { catSizeDp.toPx() }

    val soundState = rememberSoundState()
    val timeLeft = rememberTimerState(60)
    val planets = rememberPlanetsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = constraints.maxWidth.toFloat()
        val maxHeight = constraints.maxHeight.toFloat()

        val pupilRadius by animateFactor(0.01f, 0.05f, 2000)
        val whiskersAngle by animateFactor(0.01f, 0.07f, 1237)

        val gameContext = GameContext(
            planets,
            catController,
            timeLeft,
            maxWidth,
            maxHeight,
            catSizePx,
            catSizeDp,
            pupilRadius,
            whiskersAngle
        )

        NightSkyBackground(
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent {
                    val handled = catController.handleKeyEvent(it, maxWidth, maxHeight, catSizePx)
                    if (handled) checkCollisions(gameContext, soundState)
                    handled
                }
        ) {
            GameContent(gameContext)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Composable
fun GameContent(context: GameContext) {
    Box(modifier = Modifier.fillMaxSize()) {
        val planetSizeDp = context.catSizeDp * 0.6f
        context.planets.forEach { planet ->
            if (!planet.isEaten) {
                PlanetSprite(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (planet.pos.x * (context.maxWidth - context.catSizePx)).toInt(),
                                (planet.pos.y * (context.maxHeight - context.catSizePx)).toInt()
                            )
                        }
                        .size(planetSizeDp),
                    name = planet.name,
                    color = planet.color
                )
            }
        }

        CatSprite(
            modifier = Modifier
                .offset {
                    IntOffset(
                        context.catController.x.toInt(),
                        context.catController.y.toInt()
                    )
                }
                .size(context.catSizeDp),
            pupilRadiusFactor = context.pupilRadius,
            whiskersFactor = context.whiskersAngle
        )

        Greeting(name = "kot", modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp))

        Text(
            text = if (context.timeLeft > 0) context.timeLeft.toString() else "Next level",
            color = Color.Magenta,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun rememberSoundState(): Pair<SoundPool, Int> {
    val context = LocalContext.current
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
            )
            .build()
    }
    val soundId = remember { soundPool.load(context, R.raw.meow, 1) }
    DisposableEffect(Unit) { onDispose { soundPool.release() } }
    return soundPool to soundId
}

@Composable
fun rememberTimerState(initialTime: Int): Int {
    var timeLeft by remember { mutableIntStateOf(initialTime) }
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }
    return timeLeft
}

@Composable
fun rememberPlanetsState(): SnapshotStateList<PlanetData> {
    val planets = listOf(
        "Mercury" to Color(0xFF9E9E9E), "Venus" to Color(0xFFFDD835),
        "Earth" to Color(0xFF2196F3), "Mars" to Color(0xFFF44336),
        "Jupiter" to Color(0xFFFFB74D), "Saturn" to Color(0xFFFFF176),
        "Uranus" to Color(0xFF80DEEA), "Neptune" to Color(0xFF3F51B5)
    )
    val (x, y) = planetLocations(planets)
    return remember {
        mutableStateListOf<PlanetData>().apply {
            addAll(planets.mapIndexed { i, (name, color) ->
                PlanetData(name, color, Offset(x[i], y[i]))
            })
        }
    }
}

@Composable
private fun planetLocations(planets: List<Pair<String, Color>>): Pair<Array<Float>, Array<Float>> {
    val x = Array(planets.size) { 0f }
    val y = Array(planets.size) { 0f }
    val minDist = 0.1f
    for (i in 0..<planets.size) {
        do {
            x[i] = Random.nextFloat()
            y[i] = Random.nextFloat()
            var collision = false
            for (j in 0..<i) {
                val dist = sqrt((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j]))
                collision = collision or (dist < minDist)
            }
        } while (collision)
    }
    return Pair(x, y)
}

@Composable
fun animateFactor(initial: Float, target: Float, duration: Int): State<Float> {
    val transition = rememberInfiniteTransition(label = "factorTransition")
    return transition.animateFloat(
        initialValue = initial,
        targetValue = target,
        animationSpec = infiniteRepeatable(
            animation = tween(duration),
            repeatMode = RepeatMode.Reverse
        ),
        label = "factor"
    )
}

fun checkCollisions(
    context: GameContext,
    sound: Pair<SoundPool, Int>
) {
    val catCenterX = context.catController.x + context.catSizePx / 2
    val catCenterY = context.catController.y + context.catSizePx / 2
    val planetSizePx = context.catSizePx * 0.6f
    val minDist = context.catSizePx * 0.27f + planetSizePx * 0.27f

    context.planets.forEachIndexed { index, planet ->
        if (!planet.isEaten) {
            val pX = planet.pos.x * (context.maxWidth - context.catSizePx)
            val pY = planet.pos.y * (context.maxHeight - context.catSizePx)

            val pCenterX = pX + planetSizePx / 2
            val pCenterY = pY + planetSizePx / 2

            val dist =
                sqrt((catCenterX - pCenterX) * (catCenterX - pCenterX) + (catCenterY - pCenterY) * (catCenterY - pCenterY))
            if (dist < minDist) {
                context.planets[index] = planet.copy(isEaten = true)
                sound.first.play(sound.second, 1f, 1f, 0, 0, 1f)
            }
        }
    }
}

@Composable
fun NightSkyBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val skyColors =
        listOf(Color(0xFF020111), Color(0xFF020111), Color(0xFF050a30), Color(0xFF000c40))
    Box(modifier = modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(skyColors))) {
        val stars = remember {
            List(300) {
                Offset(
                    Random.nextFloat(),
                    Random.nextFloat()
                ) to Random.nextFloat()
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { (pos, alpha) ->
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = 2f,
                    center = Offset(pos.x * size.width, pos.y * size.height)
                )
            }
        }
        content()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Kiedy $name nadchodzi, co zjada planety", modifier = modifier, color = Color.Cyan)
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
