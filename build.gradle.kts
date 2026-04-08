plugins {
    // Declared here but NOT applied — subprojects apply them themselves
    id("com.android.application")             apply false
    id("com.android.library")                 apply false
    id("org.jetbrains.kotlin.android")        apply false
    id("org.jetbrains.kotlin.plugin.compose") apply false
    id("com.google.dagger.hilt.android")      apply false
    id("com.google.devtools.ksp")             apply false
}