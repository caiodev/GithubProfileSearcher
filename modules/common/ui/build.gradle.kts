import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.androidLibrary)
    alias(dep.plugins.composeCompiler)
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
        viewBinding = true
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

    namespace = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui"
}

dependencies {
    implementation(projects.modules.common.di) { because("DI Module") }
    implementation(projects.modules.common.domain) { because("Domain Module") }
    implementation(projects.modules.common.resources) { because("Resources Module") }
    implementation(projects.modules.common.utils) { because("Utils Module") }
    androidTestImplementation(projects.modules.common.testing.instrumented) { because("Android Testing Module") }
    testImplementation(projects.modules.common.testing.jvm) { because("JVM Testing Module") }

    api(dep.appCompat)
    api(dep.bundles.coil)

    // Compose
    api(dep.bundles.compose)
    api(dep.koinCompose)
    debugApi(dep.uiToolingCompose)

    api(dep.bundles.lifecycle)
    api(dep.bundles.paging)
    api(dep.bundles.views)
    api(dep.ktxCore)
    api(dep.materialDesign)

    androidTestImplementation(dep.bundles.composeTest)
}

tasks.withType<Test> {
    systemProperty("includeAndroidResources", "true")
    useJUnitPlatform()
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui"
