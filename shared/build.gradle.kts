import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Shared module for WeTheGoverned"
        homepage = "https://github.com/wetheGoverned/WETHEGOVERNED"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        val ktorVersion = "2.3.12"
        val jupnpVersion = "3.0.4"
        val lifecycleVersion = "2.8.2"
        val navigationVersion = "2.8.0-alpha10"
        
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // Jetpack Multiplatform Navigation
                implementation("org.jetbrains.androidx.navigation:navigation-compose:$navigationVersion")
                
                // Jetpack Multiplatform Lifecycle
                api("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")

                // Ktor Client
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation("javax.inject:javax.inject:1")
                
                // QR Code Generation
                implementation("io.github.g0dkar:qrcode-kotlin:4.1.1")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
            }
        }
        
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.7.0")
                api("androidx.activity:activity-compose:1.9.0")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("com.google.accompanist:accompanist-permissions:0.34.0")
                
                // SMTP support for Android
                implementation("com.sun.mail:jakarta.mail:2.0.1")

                // JVM-only dependencies
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
                implementation("org.web3j:core:4.11.0")
                api("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.8.15")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
                implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.5.6")
                
                implementation("org.jupnp:org.jupnp:$jupnpVersion")
                implementation("org.jupnp:org.jupnp.support:$jupnpVersion")
                
                // Real SMTP support for hMailServer (PC Version)
                implementation("com.sun.mail:jakarta.mail:2.0.1")

                // JVM-only dependencies
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
                implementation("org.web3j:core:4.11.0")
                api("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
            }
        }
    }
}

android {
    namespace = "net.wetheGoverned.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 24 // Synchronized with :app for legacy device support
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "net.wetheGoverned.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "net.wetheGoverned"
            packageVersion = "1.0.0"
        }
    }
}
