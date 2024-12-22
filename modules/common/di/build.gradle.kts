import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.androidLibrary)
    alias(dep.plugins.kotlinPlugin)
}

android {
    compileSdk =
        dep.versions.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            dep.versions.minSdk
                .get()
                .toInt()
    }

    kotlin {
        jvmToolchain(JvmTarget.JVM_23.target.toInt())
    }

    packaging {
        resources {
            excludes +=
                setOf(
                    "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md,licenses/ASM}",
                    "win32-x86{/attach_hotspot_windows.dll,-64/attach_hotspot_windows.dll}",
                    "kotlin{/annotation/annotation,/collections/collections,/coroutines/coroutines," +
                        "/internal/internal,/kotlin,/ranges/ranges,/reflect/reflect}.kotlin_builtins",
                )
        }
    }

    namespace = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.di"
}

dependencies {
    api(dep.bundles.koin)
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.di"
