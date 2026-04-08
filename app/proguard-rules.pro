# ─────────────────────────────────────────────────────────────────────────────
# WeTheGoverned ProGuard / R8 rules
# app/proguard-rules.pro
# ─────────────────────────────────────────────────────────────────────────────

# ── Kotlin / Coroutines ───────────────────────────────────────────────────────
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-keep class kotlinx.coroutines.** { *; }

# ── kotlinx.serialization ─────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class net.wetheGoverned.** {
    @kotlinx.serialization.Serializable *;
}
# Keep all @Serializable surrogate classes (used in CivicSerializers)
-keep @kotlinx.serialization.Serializable class * { *; }

# ── Room database ─────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# ── Hilt ─────────────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# ── Ktor (OkHttp engine + WebSockets) ────────────────────────────────────────
-keep class io.ktor.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn io.ktor.**

# ── secp256k1-kmp (Acinq native JNI library) ─────────────────────────────────
-keep class fr.acinq.secp256k1.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}

# ── WeTheGoverned domain models ───────────────────────────────────────────────
# Keep all domain model data classes (used in JSON serialisation to/from relay)
-keep class net.wetheGoverned.model.** { *; }
-keep class net.wetheGoverned.remote.** { *; }
-keep class net.wetheGoverned.local.entity.** { *; }

# ── Signed Nostr event (WebSocket payload — must not be renamed) ──────────────
-keep class net.wetheGoverned.core.SignedNostrEvent { *; }
-keep class net.wetheGoverned.core.UnsignedNostrEvent { *; }

# ── Bech32 / crypto utilities ─────────────────────────────────────────────────
-keep class net.wetheGoverned.core.Bech32Codec { *; }
-keep class net.wetheGoverned.core.Secp256k1KeyManager { *; }
-keep class net.wetheGoverned.core.NostrEventSigner { *; }

# ── Session data (must survive obfuscation — stored by name in EncryptedPrefs) ─
-keep class net.wetheGoverned.session.Session { *; }
-keepclassmembers enum net.wetheGoverned.model.VerificationTier { *; }
-keepclassmembers enum net.wetheGoverned.model.PollStatus { *; }
-keepclassmembers enum net.wetheGoverned.model.MetricSource { *; }

# ── Coil (image loading) ──────────────────────────────────────────────────────
-keep class coil.** { *; }
-dontwarn coil.**

# ── AndroidX Security (EncryptedSharedPreferences) ───────────────────────────
-keep class androidx.security.crypto.** { *; }

# ── Accompanist permissions ───────────────────────────────────────────────────
-keep class com.google.accompanist.** { *; }

# ── Suppress common third-party warnings ─────────────────────────────────────
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
