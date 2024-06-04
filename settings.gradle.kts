pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(uri("https://jitpack.io"))
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(uri("https://jitpack.io"))
    }
}

rootProject.name = "iCore"
include(":app",":isseverCore")
