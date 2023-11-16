package repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.TestSteps
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.api.MockedAPIResponseProvider.PROFILE_INFO_CALL_RESULT
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.api.factory.APITestService.newInstance
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.api.factory.APITestService.setup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Error as Error

class RemoteRepositoryTest : TestSteps {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var userProfile: UserProfileCall
    private lateinit var remoteRepository: RemoteFetcher

    @OptIn(ExperimentalSerializationApi::class)
    @BeforeEach
    override fun setupDependencies() {
        mockWebServer = setup()
        userProfile = newInstance()
        remoteRepository =
            RemoteFetcher()
    }

    @AfterEach
    fun teardown() {
        mockWebServer.shutdown()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsASuccessfulResponse200() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        PROFILE_INFO_CALL_RESULT,
                    ),
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideUsers("torvalds", 1, 20)
                response = remoteRepository.call { userProfile }
            }
        }

        then {
            var isSuccess = false
            if (response is Success<*>) isSuccess = true

            assertEquals(true, isSuccess)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsASuccessfulResponse204NoContent() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(204)
                    .setBody(
                        "",
                    ),
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideUsers("torvalds", 1, 20)
                response = remoteRepository.call { userProfile }
            }
        }

        then {
            var isSuccess = false
            if (response is Success<*>) isSuccess = true

            assertEquals(true, isSuccess)
            assertEquals(1, (response as Success<Int>).data)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsAnUnsuccessfulResponse403Forbidden() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(403)
                    .setBody(
                        "",
                    ),
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideUsers("torvalds", 1, 20)
                response = remoteRepository.call { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(9, (response as Error).error)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsAnUnsuccessfulResponse400Till402And404Till494() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(451)
                    .setBody(
                        "",
                    ),
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideUsers("torvalds", 1, 20)
                response = remoteRepository.call { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(7, (response as Error).error)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsAnUnsuccessfulResponse500Till598() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(530)
                    .setBody(
                        "",
                    ),
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideUsers("torvalds", 1, 20)
                response = remoteRepository.call { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(8, (response as Error).error)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsAnUnsuccessfulResponseGenericError() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(750)
                    .setBody(
                        "",
                    ),
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideUsers("torvalds", 1, 20)
                response = remoteRepository.call { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(10, (response as Error).error)
        }
    }

    @Test
    fun callApi__connectException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.call { setupException(ConnectException()) }
            }
        }

        then {
            assertEquals(2, (response as Error).error)
        }
    }

    @Test
    fun callApi__genericException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.call { setupException() }
            }
        }

        then {
            assertEquals(3, (response as Error).error)
        }
    }

    @Test
    fun callApi__socketTimeoutException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.call { setupException(SocketTimeoutException()) }
            }
        }

        then {
            assertEquals(4, (response as Error).error)
        }
    }

    @Test
    fun callApi__sslHandshakeException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.call { setupException(SSLHandshakeException("")) }
            }
        }

        then {
            assertEquals(5, (response as Error).error)
        }
    }

    @Test
    fun callApi__unknownHostException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.call { setupException(UnknownHostException()) }
            }
        }

        then {
            assertEquals(6, (response as Error).error)
        }
    }

    private fun setupException(exception: IOException = IOException()): Response<Profile> {
        throwException(exception)
        return listOf<Response<Profile>>()[0]
    }

    private fun throwException(exception: IOException) {
        throw exception
    }
}
