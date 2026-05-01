package ch.modulo.cateatsplanet.sprites

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun CatSprite(
    modifier: Modifier = Modifier,
    pupilRadiusFactor: Float = 0.02f,
    whiskersFactor: Float = 0.02f
) {
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
            radius = w * pupilRadiusFactor,
            center = Offset(w * 0.4f, h * 0.45f)
        )
        drawCircle(
            color = Color.Black,
            radius = w * pupilRadiusFactor,
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

        drawMouth(w, h)

        drawWhiskers(w, h, whiskersFactor)
    }
}

private fun DrawScope.drawWhiskers(
    w: Float,
    h: Float,
    whiskersFactor: Float
) {
    for (i in -1..1)
        drawLine(
            color = Color.DarkGray,
            start = Offset(w * 0.3f, h * (0.56f + whiskersFactor * i)),
            end = Offset(w * 0.7f, h * (0.56f - whiskersFactor * i)),
            strokeWidth = 0.005f * w,
        )
}

private fun DrawScope.drawMouth(
    w: Float,
    h: Float
) {
    drawArc(
        color = Color.Black,
        startAngle = 0f,    // Starts on the right (3 on a clock)
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(w * 0.35f, h * 0.52f),
        size = Size(w * 0.15f, h * 0.15f),
        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
    )
    drawArc(
        color = Color.Black,
        startAngle = 180f,
        sweepAngle = -150f,
        useCenter = false,
        topLeft = Offset(w * 0.50f, h * 0.52f),
        size = Size(w * 0.15f, h * 0.15f),
        style = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
    )
}