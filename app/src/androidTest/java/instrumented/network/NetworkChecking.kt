package instrumented.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.checkIfInternetConnectionIsAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking.observeInternetConnectionAvailability
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import utils.base.coroutines.junit4.CoroutinesTestRule
import utils.base.liveData.junit4.LiveDataTestUtil

@RunWith(AndroidJUnit4::class)
class NetworkChecking {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val testRule = CoroutinesTestRule()

    @Test
    fun checkIfInternetConnectionIsAvailable_applicationContext_isOnline() {
        var isOnline = false

        runBlocking {
            checkIfInternetConnectionIsAvailable(
                InstrumentationRegistry.getInstrumentation().targetContext,
                { isOnline = true },
                {}
            )
        }

        assertEquals(true, isOnline)
    }

    @Test
    fun checkIfInternetConnectionIsAvailable_applicationContext_isOffline() {
        var isOffline = false

        runBlocking {
            checkIfInternetConnectionIsAvailable(
                InstrumentationRegistry.getInstrumentation().targetContext,
                {},
                { isOffline = true }
            )
        }

        assertEquals(true, isOffline)
    }

    @Test
    fun internetConnectionAvailabilityObservable_applicationContext_isOnline() {
        assertEquals(
            true,
            LiveDataTestUtil.getValue(
                observeInternetConnectionAvailability(
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
                observeInternetConnectionAvailability(
                    InstrumentationRegistry.getInstrumentation().targetContext
                )
            )
        )
    }
}
