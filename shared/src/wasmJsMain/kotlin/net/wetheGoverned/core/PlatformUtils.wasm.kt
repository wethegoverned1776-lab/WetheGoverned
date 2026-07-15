package net.wetheGoverned.core

import kotlin.random.Random

actual fun randomUUID(): String {
    return (1..4).joinToString("-") { 
        Random.nextLong().toString(16).take(8)
    }
}
