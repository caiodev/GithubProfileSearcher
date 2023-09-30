package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.coroutines.junit4

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.coroutines.ContinuationInterceptor

@ExperimentalCoroutinesApi
class CoroutinesTestRule : TestRule, TestCoroutineScope by TestCoroutineScope() {
    val dispatcher = coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                Dispatchers.setMain(dispatcher)

                // everything above this happens before the test
                base.evaluate()
                // everything below this happens after the test

                cleanupTestCoroutines()
                Dispatchers.resetMain()
            }
        }
    }
}
