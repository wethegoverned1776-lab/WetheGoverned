pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application")           version "8.13.2"
        id("com.android.library")               version "8.13.2"
        id("org.jetbrains.kotlin.android")      version "2.0.21"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
        id("com.google.dagger.hilt.android")    version "2.51.1"
        id("com.google.devtools.ksp")           version "2.0.21-1.0.26"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://artifacts.consensys.net/public/maven/maven/") }
    }
}

rootProject.name = "WeTheGoverned"
include(":app")
