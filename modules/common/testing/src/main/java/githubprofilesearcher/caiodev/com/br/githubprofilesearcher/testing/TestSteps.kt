package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing

interface TestSteps {
    fun setupDependencies()

    fun given(given: () -> Unit) {
        given()
    }

    fun doWhen(doWhen: () -> Unit) {
        doWhen()
    }

    fun then(then: () -> Unit) {
        then()
    }
}
