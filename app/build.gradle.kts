import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.androidApplication)
    alias(dep.plugins.kotlinPlugin)
}

apply {
    from("$rootDir/flavors/flavors.gradle")
}

android {
    compileSdk =
        dep.versions.compileSdk
            .get()
            .toInt()
    defaultConfig {
        applicationId = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.app"
        minSdk =
            dep.versions.minSdk
                .get()
                .toInt()
        versionCode =
            dep.versions.versionCode
                .get()
                .toInt()
        versionName = dep.versions.versionName.get()
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }

        release {
            applicationIdSuffix = ".release"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro",
            )
        }
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

    namespace = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.app"
}

dependencies {
    debugImplementation(dep.leakCanary)
    implementation(projects.modules.common.di) { because("DI Module") }
    implementation(projects.modules.common.resources) { because("Resources Module") }
    implementation(projects.modules.features.profile) { because("Profile Module") }
    implementation(dep.bundles.coil)
}

tasks.withType<Test> {
    systemProperty("includeAndroidResources", "true")
    useJUnitPlatform()
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.app"
