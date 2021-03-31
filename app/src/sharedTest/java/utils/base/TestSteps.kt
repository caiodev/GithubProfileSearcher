package utils.base

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
