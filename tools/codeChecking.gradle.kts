val codeChecking by tasks.registering {
    dependsOn("clean", "ktlintFormat", "detektMain", "detektTest", "jacocoReport")
}
