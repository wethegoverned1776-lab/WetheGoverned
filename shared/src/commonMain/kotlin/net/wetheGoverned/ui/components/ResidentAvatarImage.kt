package net.wetheGoverned.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ResidentAvatarImage(
    displayName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    val initial = displayName.firstOrNull()?.uppercase() ?: "?"
    val bgColor = avatarBackgroundColor(displayName)

    // Simplified for Multiplatform - No Coil in commonMain
    LetterAvatar(initial, size, bgColor, modifier)
}

@Composable
private fun LetterAvatar(
    initial: String,
    size: Dp,
    bgColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = initial,
            style = if (size >= 56.dp) MaterialTheme.typography.headlineMedium
                    else MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun avatarBackgroundColor(name: String): androidx.compose.ui.graphics.Color {
    val palette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
    )
    val index = (name.hashCode() and Int.MAX_VALUE) % palette.size
    return palette[index]
}

@Composable
fun SmallAvatarImage(
    displayName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) = ResidentAvatarImage(displayName, avatarUrl, modifier, size = 36.dp)
