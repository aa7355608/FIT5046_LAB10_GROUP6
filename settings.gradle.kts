pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // 如果你不是通过 libs.versions.toml 管理这些插件版本，也可以在这里声明它们的版本
        id("com.android.application")         version "7.4.2" apply false
        id("org.jetbrains.kotlin.android")    version "1.8.21" apply false
        id("com.google.gms.google-services")  version "4.3.15" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HealthReminderApp"
include(":app")
