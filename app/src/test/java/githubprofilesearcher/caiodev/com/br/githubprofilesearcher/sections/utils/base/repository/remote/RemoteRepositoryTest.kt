package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
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
import utils.base.network.MockedAPIResponsesProvider.profileInfoCallResult
import utils.base.network.factory.RetrofitTestService.createRetrofitService
import utils.base.network.factory.RetrofitTestService.setup
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class RemoteRepositoryTest : TestSteps {

    @PublishedApi
    internal lateinit var mockWebServer: MockWebServer
    private lateinit var userProfile: UserProfile
    private lateinit var remoteRepository: RemoteRepository

    @ExperimentalSerializationApi
    @BeforeEach
    override fun setupDependencies() {
        mockWebServer = setup()
        userProfile = createRetrofitService()
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
            if (response is APICallResult.Success<*>) isSuccess = true

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
            if (response is APICallResult.Success<*>) isSuccess = true

            assertEquals(true, isSuccess)
            assertEquals(1, (response as APICallResult.Success<Int>).data)
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
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(9, (response as APICallResult.Error).error)
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
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(7, (response as APICallResult.Error).error)
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
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(8, (response as APICallResult.Error).error)
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
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(10, (response as APICallResult.Error).error)
        }
    }

    @Test
    fun callApi__connectException() {

        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { throwException(ConnectException()) }
            }
        }

        then {
            assertEquals(2, (response as APICallResult.Error).error)
        }
    }

    @Test
    fun callApi__genericException() {

        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { throwException() }
            }
        }

        then {
            assertEquals(3, (response as APICallResult.Error).error)
        }
    }

    @Test
    fun callApi__socketTimeoutException() {

        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { throwException(SocketTimeoutException()) }
            }
        }

        then {
            assertEquals(4, (response as APICallResult.Error).error)
        }
    }

    @Test
    fun callApi__sslHandshakeException() {

        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { throwException(SSLHandshakeException("")) }
            }
        }

        then {
            assertEquals(5, (response as APICallResult.Error).error)
        }
    }

    @Test
    fun callApi__unknownHostException() {

        var response = Any()

        doWhen {
            runBlocking {
                response =
                    remoteRepository.callApi { throwException(UnknownHostException()) }
            }
        }

        then {
            assertEquals(6, (response as APICallResult.Error).error)
        }
    }

    @Suppress("UNREACHABLE_CODE", "CAST_NEVER_SUCCEEDS")
    fun throwException(
        exception: Exception = Exception()
    ): Response<GithubProfilesList> {
        throw exception
        return exception as Response<GithubProfilesList>
    }
}
