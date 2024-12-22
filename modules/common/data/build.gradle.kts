import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.androidLibrary)
    alias(dep.plugins.kotlinPlugin)
    alias(dep.plugins.ksp)
    alias(dep.plugins.serialization)
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    kotlin {
        jvmToolchain(JvmTarget.JVM_23.target.toInt())
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
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

    flavorDimensions.add("version")

    productFlavors {
        create("demo") {
            dimension = "version"
            buildConfigField("String", "BASE_URL", "\"https://api.github.com/\"")
        }

        create("full") {
            dimension = "version"
            buildConfigField("String", "BASE_URL", "\"https://api.github.com/\"")
        }
    }

    namespace = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data"
}

dependencies {
    implementation(projects.modules.common.di) { because("DI Module") }
    implementation(projects.modules.common.domain) { because("Domain Module") }
    implementation(projects.modules.common.resources) { because("Resources Module") }
    implementation(projects.modules.common.utils) { because("Utils Module") }
    androidTestImplementation(projects.modules.common.testing.instrumented) { because("Android Testing Module") }
    testImplementation(projects.modules.common.testing.jvm) { because("JVM Testing Module") }

    implementation(dep.bundles.apiConnection)

    // Logging
    debugImplementation(dep.debugChucker)
    releaseImplementation(dep.releaseChucker)

    implementation(dep.preferencesDataStore)

    // Room
    implementation(dep.room)
    ksp(dep.roomCompiler)

    // WorkManager
    implementation(dep.bundles.workManager)

    androidTestImplementation(dep.workManagerTest)
}

tasks.withType<Test> {
    systemProperty("includeAndroidResources", "true")
    useJUnitPlatform()
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data"
