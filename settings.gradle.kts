@file:Suppress("UnstableApiUsage")

rootProject.name = "GithubProfileSearcher"

dependencyResolutionManagement {
    defaultLibrariesExtensionName.set("dep")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":modules:common:data")
include(":modules:common:di")
include(":modules:common:domain")
include(":modules:common:resources")
include(":modules:common:testing:instrumented")
include(":modules:common:testing:jvm")
include(":modules:common:testing:testing")
include(":modules:common:ui")
include(":modules:common:utils")
include(":modules:features:profile")
