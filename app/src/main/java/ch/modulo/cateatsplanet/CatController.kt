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

class CatController(initialX: Float, initialY: Float) {
    var x by mutableFloatStateOf(initialX)
        private set
    var y by mutableFloatStateOf(initialY)
        private set

    private var step = 30f

    fun handleKeyEvent(event: KeyEvent, maxWidth: Float, maxHeight: Float, catSize: Float): Boolean {
        if (event.type != KeyEventType.KeyDown) return false

        when (event.key) {
            Key.DirectionUp -> y = (y - step).coerceAtLeast(0f)
            Key.DirectionDown -> y = (y + step).coerceAtMost(maxHeight - catSize)
            Key.DirectionLeft -> x = (x - step).coerceAtLeast(0f)
            Key.DirectionRight -> x = (x + step).coerceAtMost(maxWidth - catSize)
            else -> return false
        }
        return true
    }
}

@Composable
fun rememberCatController(initialX: Float = 500f, initialY: Float = 500f): CatController {
    return remember { CatController(initialX, initialY) }
}
