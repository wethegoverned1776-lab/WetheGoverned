plugins {
    // Declared here but NOT applied — subprojects apply them themselves
    id("com.android.application")             version "8.13.2" apply false
    id("com.android.library")                 version "8.13.2" apply false
    id("org.jetbrains.kotlin.android")        version "2.0.21" apply false
    id("org.jetbrains.kotlin.multiplatform")   version "2.0.21" apply false
    id("org.jetbrains.compose")               version "1.7.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("com.google.dagger.hilt.android")    version "2.51.1" apply false
    id("com.google.devtools.ksp")           version "2.0.21-1.0.26" apply false
    id("com.google.gms.google-services")    version "4.4.2" apply false
}

tasks.register("updateProjectMaster") {
    group = "documentation"
    val masterFile = file("PROJECT_MASTER.md")
    val wrapperFile = file("gradle/wrapper/gradle-wrapper.properties")
    val settingsFile = file("settings.gradle.kts")

    doLast {
        if (!masterFile.exists()) return@doLast
        var content = masterFile.readText()
        val now = java.time.LocalDate.now().toString()

        val gradleVersion = wrapperFile.readLines()
            .find { it.startsWith("distributionUrl") }
            ?.let { Regex("gradle-(.*?)-(bin|all).zip").find(it)?.groupValues?.get(1) } ?: "8.13"

        val settingsText = settingsFile.readText()
        val kotlinVersion = Regex("""id\("org.jetbrains.kotlin.multiplatform"\)\s+version\s+"(.*?)"""").find(settingsText)?.groupValues?.get(1) ?: "2.0.21"
        val agpVersion = Regex("""id\("com.android.application"\)\s+version\s+"(.*?)"""").find(settingsText)?.groupValues?.get(1) ?: "8.13.2"
        val composeVersion = Regex("""id\("org.jetbrains.compose"\)\s+version\s+"(.*?)"""").find(settingsText)?.groupValues?.get(1) ?: "1.7.0"

        content = content.replace(Regex("<!-- DATE_START -->.*?<!-- DATE_END -->"), "<!-- DATE_START -->$now<!-- DATE_END -->")
        content = content.replace(Regex("<!-- GRADLE_VERSION_START -->.*?<!-- GRADLE_VERSION_END -->"), "<!-- GRADLE_VERSION_START -->Gradle ($gradleVersion)<!-- GRADLE_VERSION_END -->")

        val buildConfig = """
- **Gradle Version:** $gradleVersion
- **Kotlin Version:** $kotlinVersion
- **AGP Version:** $agpVersion
- **Key Dependencies:** Ktor (Networking), Compose Multiplatform ($composeVersion), kotlinx-serialization, kotlinx-datetime.
- **Compatibility:** iOS targets (iosX64, iosArm64, iosSimulatorArm64) configured via CocoaPods.
- **Platform Specifics:** JVM-only dependencies (Ktor Server, Web3j) isolated to `androidMain` and `desktopMain`.
""".trim()
        content = content.replace(Regex("(?s)<!-- BUILD_CONFIG_START -->.*?<!-- BUILD_CONFIG_END -->"), "<!-- BUILD_CONFIG_START -->\n$buildConfig\n<!-- BUILD_CONFIG_END -->")

        masterFile.writeText(content)
    }
}

// Ensure every subproject's assemble task triggers the update
subprojects {
    tasks.matching { it.name == "assemble" }.configureEach {
        dependsOn(":updateProjectMaster")
    }
}
