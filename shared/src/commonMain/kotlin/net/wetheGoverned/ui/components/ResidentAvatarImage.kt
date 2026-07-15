package net.wetheGoverned.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

// ─────────────────────────────────────────────────────────────────────────────
// ResidentAvatarImage
//
// Replaces the "Avatar placeholder – real impl uses Coil AsyncImage" comment
// in ResidentProfileScreen.kt with a production-ready component.
//
// Behaviour:
//   • If avatarUrl is non-null → loads with Coil (disk + memory cache)
//   • Falls back to a letter avatar (initials on coloured circle) while loading
//     or if URL fails
//   • Crossfade animation on load
//   • Supports arbitrary sizes via the `size` parameter
//
// Dependency: implementation("io.coil-kt:coil-compose:2.6.0")
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ResidentAvatarImage(
    displayName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    val shape   = MaterialTheme.shapes.extraLarge
    val initial = displayName.firstOrNull()?.uppercase() ?: "?"
    val bgColor = avatarBackgroundColor(displayName)

    if (avatarUrl != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .memoryCacheKey(avatarUrl)
                .diskCacheKey(avatarUrl)
                .build(),
            contentDescription = "$displayName avatar",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape),
            loading = {
                // Show letter avatar while image loads
                LetterAvatar(initial, size, bgColor, modifier)
            },
            error = {
                // Show letter avatar on load failure
                LetterAvatar(initial, size, bgColor, modifier)
            },
        )
    } else {
        LetterAvatar(initial, size, bgColor, modifier)
    }
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

/**
 * Deterministic background colour from the first characters of the display name.
 * Ensures the same resident always gets the same colour without storing preferences.
 */
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

// ─────────────────────────────────────────────────────────────────────────────
// SmallAvatarImage – compact version for feed rows, Q&A threads, etc.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SmallAvatarImage(
    displayName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) = ResidentAvatarImage(displayName, avatarUrl, modifier, size = 36.dp)
