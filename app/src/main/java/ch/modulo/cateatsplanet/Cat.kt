package ch.modulo.cateatsplanet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

/**
 * CatController using normalized coordinates (0.0f to 1.0f)
 */
class Cat(initialX: Float, initialY: Float) {
    var x by mutableFloatStateOf(initialX)
        private set
    var y by mutableFloatStateOf(initialY)
        private set

    // Normalized step size (fraction of the screen)
    private val step = 0.02f

    fun handleKeyEvent(
        event: KeyEvent,
        maxWidth: Float,
        maxHeight: Float,
        catSizePx: Float
    ): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        // Calculate steps relative to screen size to keep movement feeling consistent
        // but we constrain the result in the 0..1 range.
        // x and y represent the top-left of the cat in normalized space.
        val xLimit = 1f - (catSizePx / maxWidth)
        val yLimit = 1f - (catSizePx / maxHeight)

        when (event.key) {
            Key.DirectionUp -> y = (y - step).coerceIn(0f, yLimit)
            Key.DirectionDown -> y = (y + step).coerceIn(0f, yLimit)
            Key.DirectionLeft -> x = (x - step).coerceIn(0f, xLimit)
            Key.DirectionRight -> x = (x + step).coerceIn(0f, xLimit)
            else -> return false
        }
        return true
    }
}

@Composable
fun rememberCatController(initialX: Float = 0.5f, initialY: Float = 0.5f): Cat {
    return remember { Cat(initialX, initialY) }
}
