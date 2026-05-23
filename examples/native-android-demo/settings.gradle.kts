pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GlanceKitNativeDemo"
include(":app")
include(":glancekit-android-core")

project(":glancekit-android-core").projectDir =
    file("../../packages/android-core")
