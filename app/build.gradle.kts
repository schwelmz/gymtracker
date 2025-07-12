
plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0" // Or your Kotlin version
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp) // Preferred if ksp is in libs.versions.toml
}

android {
    namespace = "com.example.gymtracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gymtracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.google.guava:guava:32.1.3-android")
    implementation("androidx.health.connect:connect-client:1.1.0-rc02")
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.1")
    // Room Database
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)
    // Optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation("com.google.code.gson:gson:2.13.1")
    // Charting Library - Vico
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.0")
    // Image Loading - Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

// Accompanist Permissions (ensure you get the latest version)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    // For Google's ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    // For CameraX to display a camera preview
    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")
// For Accompanist Permissions to easily handle camera permission
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

// For Retrofit (networking)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// For Kotlinx Serialization (JSON parsing)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

// For Coil (displaying images from a URL)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.camera:camera-core:1.3.3-ktx")

}