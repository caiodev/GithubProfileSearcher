package network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.internetConnectionAvailabilityObservable
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import utils.base.liveData.LiveDataTestUtil

@RunWith(AndroidJUnit4::class)
class NetworkCheckingTest {

    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun checkIfInternetConnectionIsAvailable_applicationContext_isOnline() {

        var isOnline = false

        checkIfInternetConnectionIsAvailable(
            InstrumentationRegistry.getInstrumentation().targetContext,
            { isOnline = true },
            {}
        )
        assertEquals(true, isOnline)
    }

    @Test
    fun checkIfInternetConnectionIsAvailable_applicationContext_isOffline() {

        var isOffline = false

        checkIfInternetConnectionIsAvailable(
            InstrumentationRegistry.getInstrumentation().targetContext,
            {},
            { isOffline = true }
        )
        assertEquals(true, isOffline)
    }

    @Test
    fun internetConnectionAvailabilityObservable_applicationContext_isOnline() {
        assertEquals(
            true,
            LiveDataTestUtil.getValue(
                internetConnectionAvailabilityObservable(
                    InstrumentationRegistry.getInstrumentation().targetContext
                )
            )
        )
    }

    @Test
    fun internetConnectionAvailabilityObservable_applicationContext_isOffline() {
        assertEquals(
            false,
            LiveDataTestUtil.getValue(
                internetConnectionAvailabilityObservable(
                    InstrumentationRegistry.getInstrumentation().targetContext
                )
            )
        )
    }
}
