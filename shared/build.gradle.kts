plugins {
    alias(libs.plugins.android.library)  // or kotlin("jvm") if it's a pure Kotlin module
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "net.wetheGoverned.shared"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Add common dependencies here later if needed
}