package ch.modulo.cateatsplanet

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun CatSprite(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val catColor = Color(0xFF888888) // Gray cat

        // Ears
        val leftEar = Path().apply {
            moveTo(w * 0.30f, h * 0.39f)
            lineTo(w * 0.32f, h * 0.15f)
            lineTo(w * 0.45f, h * 0.35f)
            close()
        }
        drawPath(leftEar, catColor)

        val rightEar = Path().apply {
            moveTo(w * 0.55f, h * 0.35f)
            lineTo(w * 0.68f, h * 0.15f)
            lineTo(w * 0.70f, h * 0.39f)
            close()
        }
        drawPath(rightEar, catColor)

        // Head
        drawOval(
            color = catColor,
            topLeft = Offset(w * 0.25f, h * 0.3f),
            size = Size(w * 0.5f, h * 0.4f)
        )

        // Eyes
        drawCircle(
            color = Color.Companion.Yellow,
            radius = w * 0.06f,
            center = Offset(w * 0.4f, h * 0.45f)
        )
        drawCircle(
            color = Color.Companion.Yellow,
            radius = w * 0.06f,
            center = Offset(w * 0.6f, h * 0.45f)
        )

        // Pupils
        drawCircle(
            color = Color.Companion.Black,
            radius = w * 0.02f,
            center = Offset(w * 0.4f, h * 0.45f)
        )
        drawCircle(
            color = Color.Companion.Black,
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