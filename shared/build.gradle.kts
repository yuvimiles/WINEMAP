import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // DateTime for KMP
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            // Firebase KMP
            implementation("dev.gitlive:firebase-app:1.10.4")
            implementation("dev.gitlive:firebase-auth:1.10.4")
            implementation("dev.gitlive:firebase-firestore:1.10.4")
            implementation("dev.gitlive:firebase-storage:1.10.4")
        }
        androidMain.dependencies {
            // Firebase Android SDK
            implementation("com.google.firebase:firebase-auth-ktx")
            implementation("com.google.firebase:firebase-firestore-ktx")
            implementation("com.google.firebase:firebase-storage-ktx")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

        }
    }
}

android {
    namespace = "com.example.winemap"
    compileSdk = 34
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
