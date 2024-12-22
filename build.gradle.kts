plugins {
    alias(dep.plugins.androidApplication) apply false
    alias(dep.plugins.androidLibrary) apply false
    alias(dep.plugins.composeCompiler) apply false
    alias(dep.plugins.detekt) apply false
    alias(dep.plugins.kotlinJvm) apply false
    alias(dep.plugins.kotlinPlugin) apply false
    alias(dep.plugins.ksp) apply false
    alias(dep.plugins.ktlint) apply false
    alias(dep.plugins.serialization) apply false
}

val detektVersion = dep.versions.detekt.get()
allprojects {
    apply { from("${project.rootDir}/scripts/codeChecking.gradle.kts") }
    apply { from("${project.rootDir}/scripts/coverage.gradle") }
    apply { from("${project.rootDir}/scripts/detekt.gradle") }
    apply { from("${project.rootDir}/scripts/lint.gradle") }

    dependencies {
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion")
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-rules-libraries:$detektVersion")
        add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:$detektVersion")
    }
}

tasks.register<Delete>("clean") {
    description = "Removes /build directories from project."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    delete(layout.buildDirectory)
}
