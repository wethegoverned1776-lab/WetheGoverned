package net.wetheGoverned.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * A procedurally drawn US Flag background for the WeTheGoverned platform.
 * Follows official proportions (1.9:1) but adapts to fill the screen.
 */
@Composable
fun USFlagBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.05f // Subtle by default so text remains readable
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // --- DRAW STRIPES ---
        val stripeHeight = height / 13
        val oldGloryRed = Color(0xFFB22234)
        
        for (i in 0 until 13) {
            val color = if (i % 2 == 0) oldGloryRed else Color.White
            drawRect(
                color = color,
                topLeft = Offset(0f, i * stripeHeight),
                size = Size(width, stripeHeight),
                alpha = alpha
            )
        }
        
        // --- DRAW UNION (CANTON) ---
        // Canton height = 7/13 of total height. Canton width = 0.76 of height (official ratio)
        // For background use, we might want it to scale, but let's stick to roughly official proportions
        val cantonHeight = (7f / 13f) * height
        val cantonWidth = width * 0.4f // Slightly wider for responsiveness
        val oldGloryBlue = Color(0xFF3C3B6E)
        
        drawRect(
            color = oldGloryBlue,
            topLeft = Offset.Zero,
            size = Size(cantonWidth, cantonHeight),
            alpha = alpha
        )
        
        // --- DRAW STARS (50 Stars) ---
        // Arrangement: 5 rows of 6 stars alternating with 4 rows of 5 stars
        val xSpace = cantonWidth / 12f
        val ySpace = cantonHeight / 10f
        val starRadius = xSpace * 0.4f
        
        for (row in 1..9) {
            val isEvenRow = row % 2 == 0
            val starsInRow = if (isEvenRow) 5 else 6
            val xOffset = if (isEvenRow) xSpace * 2 else xSpace
            
            for (col in 0 until starsInRow) {
                drawStar(
                    center = Offset(
                        xOffset + (col * xSpace * 2),
                        row * ySpace
                    ),
                    radius = starRadius,
                    color = Color.White,
                    alpha = alpha
                )
            }
        }
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float
) {
    val path = Path().apply {
        val angle = PI / 5
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) radius else radius * 0.382f
            val x = center.x + r * sin(i * angle).toFloat()
            val y = center.y - r * cos(i * angle).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path = path, color = color, alpha = alpha)
}
