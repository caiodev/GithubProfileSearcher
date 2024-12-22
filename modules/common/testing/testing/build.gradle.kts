import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.kotlinJvm)
    alias(dep.plugins.serialization)
}

kotlin {
    jvmToolchain(JvmTarget.JVM_23.target.toInt())
}

dependencies {
    api(dep.bundles.commonTest)
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing"
