package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import utils.base.TestSteps
import utils.base.api.MockedAPIResponseProvider.profileInfoCallResult
import utils.base.api.factory.RetrofitTestService.newInstance
import utils.base.api.factory.RetrofitTestService.setup
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepositoryTest : TestSteps {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var userProfile: UserProfile
    private lateinit var remoteRepository: RemoteRepository

    @OptIn(ExperimentalSerializationApi::class)
    @BeforeEach
    override fun setupDependencies() {
        mockWebServer = setup()
        userProfile = newInstance()
        remoteRepository =
            RemoteRepository()
    }

    @AfterEach
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsASuccessfulResponse200() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        profileInfoCallResult
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = remoteRepository.callApi { userProfile }
            }
        }

        then {
            var isSuccess = false
            if (response is States.Success<*>) isSuccess = true

            assertEquals(true, isSuccess)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @ExperimentalCoroutinesApi
    @Test
    fun callApi__returnsASuccessfulResponse204NoContent() {
        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(204)
                    .setBody(
                        ""
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = remoteRepository.callApi { userProfile }
            }
        }

        then {
            var isSuccess = false
            if (response is States.Success<*>) isSuccess = true

            assertEquals(true, isSuccess)
            assertEquals(1, (response as States.Success<Int>).data)
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
                        ""
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = remoteRepository.callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is States.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(9, (response as States.Error).error)
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
                        ""
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = remoteRepository.callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is States.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(7, (response as States.Error).error)
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
                        ""
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = remoteRepository.callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is States.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(8, (response as States.Error).error)
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
                        ""
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = remoteRepository.callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is States.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(10, (response as States.Error).error)
        }
    }

    @Test
    fun callApi__connectException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { setupException(ConnectException()) }
            }
        }

        then {
            assertEquals(2, (response as States.Error).error)
        }
    }

    @Test
    fun callApi__genericException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { setupException() }
            }
        }

        then {
            assertEquals(3, (response as States.Error).error)
        }
    }

    @Test
    fun callApi__socketTimeoutException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { setupException(SocketTimeoutException()) }
            }
        }

        then {
            assertEquals(4, (response as States.Error).error)
        }
    }

    @Test
    fun callApi__sslHandshakeException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { setupException(SSLHandshakeException("")) }
            }
        }

        then {
            assertEquals(5, (response as States.Error).error)
        }
    }

    @Test
    fun callApi__unknownHostException() {
        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { setupException(UnknownHostException()) }
            }
        }

        then {
            assertEquals(6, (response as States.Error).error)
        }
    }

    private fun setupException(
        exception: IOException = IOException()
    ): Response<Profile> {
        throwException(exception)
        return listOf<Response<Profile>>()[0]
    }

    private fun throwException(
        exception: IOException
    ) {
        throw exception
    }
}
