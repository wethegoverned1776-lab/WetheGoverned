package net.wetheGoverned.core

import kotlin.random.Random

actual fun randomUUID(): String {
    return (1..4).joinToString("-") { 
        Random.nextLong().toString(16).take(8)
    }
}

actual fun sha256(input: String): String = computeSha256(input)
