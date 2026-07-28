package net.wetheGoverned.core

import java.util.UUID

import java.security.MessageDigest

actual fun randomUUID(): String = UUID.randomUUID().toString()

actual fun sha256(input: String): String = computeSha256(input)
