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

include(":modules:common:datasource:datasource")
include(":modules:common:datasource:features:profile")

include(":modules:common:midfield:midfield")
include(":modules:common:midfield:features:profile")

include(":modules:common:testing:instrumented")
include(":modules:common:testing:jvm")
include(":modules:common:testing:testing")

include(":modules:common:ui:ui")
include(":modules:common:ui:features:profile")

include(":modules:features:profile")
