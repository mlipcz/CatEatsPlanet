package ch.modulo.cateatsplanet

import android.annotation.SuppressLint
import android.os.Bundle
import android.media.AudioAttributes
import android.media.SoundPool
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
import androidx.compose.runtime.*
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

        NightSkyBackground(
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent {
                    val handled = catController.handleKeyEvent(it, maxWidth, maxHeight, catSizePx)
                    if (handled) checkCollisions(catController, planets, maxWidth, maxHeight, catSizePx, soundState)
                    handled
                }
        ) {
            GameContent(planets, catController, timeLeft, maxWidth, maxHeight, catSizePx, catSizeDp, pupilRadius, whiskersAngle)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Composable
fun GameContent(
    planets: List<PlanetData>,
    catController: CatController,
    timeLeft: Int,
    maxWidth: Float,
    maxHeight: Float,
    catSizePx: Float,
    catSizeDp: androidx.compose.ui.unit.Dp,
    pupilRadius: Float,
    whiskersAngle: Float
) {
    Box(modifier = Modifier.fillMaxSize()) {
        planets.forEach { planet ->
            if (!planet.isEaten) {
                PlanetSprite(
                    modifier = Modifier
                        .offset { 
                            IntOffset(
                                (planet.pos.x * (maxWidth - catSizePx)).toInt(),
                                (planet.pos.y * (maxHeight - catSizePx)).toInt()
                            )
                        }
                        .size(catSizeDp * 0.6f),
                    name = planet.name,
                    color = planet.color
                )
            }
        }

        CatSprite(
            modifier = Modifier
                .offset { IntOffset(catController.x.toInt(), catController.y.toInt()) }
                .size(catSizeDp),
            pupilRadiusFactor = pupilRadius,
            whiskersFactor = whiskersAngle
        )

        Greeting(name = "kot", modifier = Modifier.align(Alignment.BottomStart).padding(32.dp))

        Text(
            text = if (timeLeft > 0) timeLeft.toString() else "Next level",
            color = Color.Magenta,
            modifier = Modifier.align(Alignment.TopEnd).padding(32.dp)
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
fun rememberPlanetsState() = remember {
    mutableStateListOf<PlanetData>().apply {
        addAll(listOf(
            "Mercury" to Color(0xFF9E9E9E), "Venus" to Color(0xFFFDD835),
            "Earth" to Color(0xFF2196F3), "Mars" to Color(0xFFF44336),
            "Jupiter" to Color(0xFFFFB74D), "Saturn" to Color(0xFFFFF176),
            "Uranus" to Color(0xFF80DEEA), "Neptune" to Color(0xFF3F51B5)
        ).map { (name, color) -> PlanetData(name, color, Offset(Random.nextFloat(), Random.nextFloat())) })
    }
}

@Composable
fun animateFactor(initial: Float, target: Float, duration: Int): State<Float> {
    val transition = rememberInfiniteTransition(label = "factorTransition")
    return transition.animateFloat(
        initialValue = initial,
        targetValue = target,
        animationSpec = infiniteRepeatable(animation = tween(duration), repeatMode = RepeatMode.Reverse),
        label = "factor"
    )
}

fun checkCollisions(
    cat: CatController,
    planets: MutableList<PlanetData>,
    maxWidth: Float,
    maxHeight: Float,
    catSizePx: Float,
    sound: Pair<SoundPool, Int>
) {
    val catCenterX = cat.x + catSizePx / 2
    val catCenterY = cat.y + catSizePx / 2

    planets.forEachIndexed { index, planet ->
        if (!planet.isEaten) {
            val pX = planet.pos.x * (maxWidth - catSizePx)
            val pY = planet.pos.y * (maxHeight - catSizePx)
            val pCenterX = pX + (catSizePx * 0.6f) / 2
            val pCenterY = pY + (catSizePx * 0.6f) / 2

            val dist = sqrt((catCenterX - pCenterX) * (catCenterX - pCenterX) + (catCenterY - pCenterY) * (catCenterY - pCenterY))
            if (dist < (catSizePx * 0.4f + (catSizePx * 0.6f) * 0.4f)) {
                planets[index] = planet.copy(isEaten = true)
                sound.first.play(sound.second, 1f, 1f, 0, 0, 1f)
            }
        }
    }
}

@Composable
fun NightSkyBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val skyColors = listOf(Color(0xFF020111), Color(0xFF020111), Color(0xFF050a30), Color(0xFF000c40))
    Box(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(skyColors))) {
        val stars = remember { List(300) { Offset(Random.nextFloat(), Random.nextFloat()) to Random.nextFloat() } }
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { (pos, alpha) ->
                drawCircle(color = Color.White.copy(alpha = alpha), radius = 2f, center = Offset(pos.x * size.width, pos.y * size.height))
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
                CatSprite(modifier = Modifier.offset(100.dp, 100.dp).size(100.dp))
                Greeting("kot")
            }
        }
    }
}
