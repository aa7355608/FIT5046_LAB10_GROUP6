plugins {
    // 如果你在 settings.gradle.kts 中启用了 versionCatalog（libs.versions.toml）：
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)    // 若 toml 中未定义可删除此行

    // Firebase Google-Services 插件
    id("com.google.gms.google-services")
    // 如果不使用 versionCatalog，改为：
    // id("com.android.application")
    // id("org.jetbrains.kotlin.android")
    // id("org.jetbrains.kotlin.plugin.compose")
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
        // 请与项目中使用的 Compose Compiler 版本保持一致
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
    // 使用 BOM 统一管理 Compose 各模块版本
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))

    // 核心 UI
    implementation("androidx.compose.ui:ui")
    // 文本输入／文本布局 API (KeyboardOptions/KeyboardType)
    implementation("androidx.compose.ui:ui-text")
    // 布局、LazyList、手势等 Foundation
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    // Material3
    implementation("androidx.compose.material3:material3")
    // Material Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    /* ======== AndroidX 基础 ======== */
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    /* ======== 预览 & 调试 ======== */
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    /* ======== 测试 ======== */
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
