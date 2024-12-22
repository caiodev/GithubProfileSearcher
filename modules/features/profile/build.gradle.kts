import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.androidLibrary)
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

    namespace = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile"
}

dependencies {
    implementation(projects.modules.common.data) { because("Data Module") }
    implementation(projects.modules.common.di) { because("DI Module") }
    implementation(projects.modules.common.domain) { because("Domain Module") }
    implementation(projects.modules.common.resources) { because("Resources Module") }
    implementation(projects.modules.common.ui) { because("UI Module") }
    implementation(projects.modules.common.utils) { because("Utils Module") }
    androidTestImplementation(projects.modules.common.testing.instrumented) { because("Android Testing Module") }
    testImplementation(projects.modules.common.testing.jvm) { because("JVM Testing Module") }
}

tasks.withType<Test> {
    systemProperty("includeAndroidResources", "true")
    useJUnitPlatform()
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile"
