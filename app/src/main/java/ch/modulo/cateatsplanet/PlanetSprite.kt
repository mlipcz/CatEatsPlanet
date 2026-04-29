package ch.modulo.cateatsplanet

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun PlanetSprite(
    modifier: Modifier = Modifier,
    name: String,
    color: Color
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val radius = minOf(w, h) * 0.35f
        val center = Offset(w / 2f, h / 2f)

        // Main body
        drawCircle(color = color, radius = radius, center = center)

        // Features
        when (name) {
            "Saturn" -> {
                drawOval(
                    color = Color(0xFFC5AB6E).copy(alpha = 0.7f),
                    topLeft = Offset(w * 0.05f, h * 0.42f),
                    size = Size(w * 0.9f, h * 0.16f),
                    style = Stroke(width = w * 0.04f)
                )
            }
            "Jupiter" -> {
                for (i in -1..1) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.15f),
                        start = Offset(w * 0.2f, h * (0.5f + i * 0.08f)),
                        end = Offset(w * 0.8f, h * (0.5f + i * 0.08f)),
                        strokeWidth = h * 0.02f
                    )
                }
            }
            "Earth" -> {
                drawCircle(
                    color = Color(0xFF4CAF50).copy(alpha = 0.5f),
                    radius = radius * 0.4f,
                    center = Offset(center.x + radius * 0.3f, center.y - radius * 0.2f)
                )
                drawCircle(
                    color = Color(0xFF4CAF50).copy(alpha = 0.3f),
                    radius = radius * 0.3f,
                    center = Offset(center.x - radius * 0.4f, center.y + radius * 0.1f)
                )
            }
            "Mars" -> {
                // Little crater or spot
                drawCircle(
                    color = Color.Black.copy(alpha = 0.1f),
                    radius = radius * 0.2f,
                    center = Offset(center.x + radius * 0.2f, center.y + radius * 0.3f)
                )
            }
        }
    }
}
