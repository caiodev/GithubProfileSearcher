import org.gradle.kotlin.dsl.dep
import org.gradle.kotlin.dsl.projects
import org.gradle.kotlin.dsl.runtimeOnly
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(JvmTarget.JVM_23.target.toInt())
}

dependencies {
    api(projects.modules.common.testing.testing) { because("Common Testing") }
    api(dep.bundles.jvmTest)
    runtimeOnly(dep.junit5Engine)
    runtimeOnly(dep.junit5PlatformLauncher)
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.jvm"
