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
        // If you don't manage these plugin versions through libs.versions.toml, you can also declare their versions here
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
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "HealthReminderApp"
include(":app")
