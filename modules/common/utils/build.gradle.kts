import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(JvmTarget.JVM_23.target.toInt())
}

dependencies {
    testImplementation(projects.modules.common.testing.jvm) { because("JVM Testing") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.utils"
