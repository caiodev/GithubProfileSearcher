@file:Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    defaultLibrariesExtensionName.set("dep")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GithubProfileSearcher"
include(":app")
include(":modules:common:core")
include(":modules:common:datasource")
include(":modules:common:testing")
include(":modules:common:ui")
include(":modules:features:profile")