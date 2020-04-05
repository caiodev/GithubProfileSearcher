package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.callInterface.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.service.APICallResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.base.TestSteps
import utils.base.network.MockedAPIResponsesProvider.profileInfoCallResult
import utils.base.network.factory.RetrofitTestService.createRetrofitService
import utils.base.network.factory.RetrofitTestService.setup
import java.util.concurrent.TimeUnit

class RemoteRepositoryTest : TestSteps {

    @PublishedApi
    internal lateinit var mockWebServer: MockWebServer
    private lateinit var userProfile: UserProfile
    private lateinit var remoteRepository: RemoteRepository

    @UnstableDefault
    @BeforeEach
    override fun setupDependencies() {
        mockWebServer = setup()
        userProfile = createRetrofitService()
        remoteRepository = RemoteRepository()
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
            assertEquals(
                1624,
                (response as APICallResult.Success<GithubProfilesList>).data.githubProfileInformationList[0].score?.toInt()
            )
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
    fun callApi__returnsAnUnsuccessfulResponse400Till402And404Till450() {

        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(404)
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
    fun callApi__returnsAnUnsuccessfulResponse500Till598() {

        var response = Any()

        given {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(550)
                    .setBody(
                        ""
//                        getJson("sharedTest/resources/mockedJsonResponses/github_user_profile_response.json")
                    ).throttleBody(1024, 1, TimeUnit.SECONDS)
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
                    ).throttleBody(1024, 1, TimeUnit.SECONDS)
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

    @AfterEach
    fun teardown() {
        mockWebServer.shutdown()
    }
}