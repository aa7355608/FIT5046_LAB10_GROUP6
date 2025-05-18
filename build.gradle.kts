// file: <projectRoot>/build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android)   apply false
    alias(libs.plugins.kotlin.compose)   apply false

    // 只声明、不立即应用 Google Services 插件
    id("com.google.gms.google-services") version "4.3.15" apply false
}

// 注意：这里不能再出现 buildscript {}、allprojects {} 或 repositories {} 块，
// 所有仓库配置都交由 settings.gradle.kts 管理。
