package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.TestSteps

@RunWith(AndroidJUnit4::class)
class NetworkCheckingTest : TestSteps {

    override fun setupDependencies() {
        // detekt : Empty Method
    }

    @Test
    fun checkIfInternetConnectionIsAvailable_applicationContext_isOffline() {
        var isOffline = false

        runBlocking {
            checkIfInternetConnectionIsAvailable(
                InstrumentationRegistry.getInstrumentation().targetContext,
                {},
                { isOffline = true },
            )
        }

        assertEquals(
            true,
            isOffline,
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun internetConnectionAvailabilityObservable_applicationContext_isOffline() {
        var isOffline = false

        doWhen {
            runBlocking {
                isOffline = observeInternetConnectionAvailability(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                ).drop(1).first()
            }
        }

        assertEquals(
            false,
            isOffline,
        )
    }

    @Test
    fun checkIfInternetConnectionIsAvailable_applicationContext_isOnline() {
        var isOnline = false

        runBlocking {
            checkIfInternetConnectionIsAvailable(
                InstrumentationRegistry.getInstrumentation().targetContext,
                { isOnline = true },
                {},
            )
        }

        assertEquals(
            true,
            isOnline,
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun internetConnectionAvailabilityObservable_applicationContext_isOnline() {
        var isOnline = false

        doWhen {
            runBlocking {
                isOnline = observeInternetConnectionAvailability(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                ).drop(1).first()
            }
        }

        assertEquals(
            true,
            isOnline,
        )
    }
}
