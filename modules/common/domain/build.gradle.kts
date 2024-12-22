import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(dep.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(JvmTarget.JVM_23.target.toInt())
}

dependencies {
    implementation(projects.modules.common.utils) { because("Utils Module") }
    implementation(dep.bundles.koinJvm)
    testImplementation(projects.modules.common.testing.jvm) { because("JVM Testing Module") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

group = "githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain"
