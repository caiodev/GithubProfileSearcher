package githubprofilesearcher.caiodev.com.br.githubprofilesearcher

import androidx.test.ext.junit.runners.AndroidJUnit4
import base.TestSteps
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest : TestSteps {
    @Test
    @Throws(Exception::class)
    fun useAppContext() {

        // App Context under test
        val appContext =
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals(
            "githubprofilesearcher.caiodev.com.br.githubprofilesearcher",
            appContext.packageName
        )
    }
}