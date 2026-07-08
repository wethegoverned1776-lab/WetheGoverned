package net.wetheGoverned.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import qrcode.QRCode

@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    val matrix = remember(data) {
        try {
            val qrCode = QRCode(data)
            val rawData = qrCode.rawData // This is Array<Array<QRCodeSquare>>
            val size = rawData.size
            
            Array(size) { row ->
                BooleanArray(size) { col ->
                    rawData[row][col].dark
                }
            }
        } catch (e: Exception) {
            println("❌ QR Render Error: ${e.message}")
            // Return a simple empty grid or error pattern
            Array(1) { BooleanArray(1) { true } }
        }
    }

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / matrix.size
            
            for (row in 0 until matrix.size) {
                for (col in 0 until matrix.size) {
                    if (matrix[row][col]) {
                        drawRect(
                            color = color,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize + 1f, cellSize + 1f)
                        )
                    }
                }
            }
        }
    }
}
