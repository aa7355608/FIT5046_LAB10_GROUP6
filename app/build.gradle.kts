plugins {
    // If you have enabled versionCatalog (libs.versions.toml) in settings.gradle.kts：
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)    // If it is not defined in toml, this line can be deleted

    // Firebase Google-Services
    id("com.google.gms.google-services")
    // If versionCatalog is not used, change it to:
    // id("com.android.application")
    // id("org.jetbrains.kotlin.android")
    // id("org.jetbrains.kotlin.plugin.compose")

    // Directions API
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

android {
    namespace = "com.example.healthreminderapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.healthreminderapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
}

dependencies {
    /* ========== Firebase ========== */
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    /* ======== Jetpack Compose ======== */
    // Use BOM to uniformly manage the versions of each Compose module
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))

    // Core UI
    implementation("androidx.compose.ui:ui")
    // Text input/text layout API (KeyboardOptions/KeyboardType)
    implementation("androidx.compose.ui:ui-text")
    // Foundation for layout, LazyList, gestures, etc
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    // Material3
    implementation("androidx.compose.material3:material3")
    // Material Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    /* ======== AndroidX Base ======== */
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    /* ======== Preview & Debugging ======== */
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    /* ======== Testing ======== */
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Google Map Compose
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")


}
