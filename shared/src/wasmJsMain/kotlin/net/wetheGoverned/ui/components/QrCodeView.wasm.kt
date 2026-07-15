package net.wetheGoverned.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun QrCodeView(
    data: String,
    modifier: Modifier,
    color: Color,
) {
    Box(modifier = modifier) {
        Text("QR Code: $data")
    }
}
