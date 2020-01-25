package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository

import base.TestSteps
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.GithubProfilesList
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.callInterfaces.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.factory.RetrofitTestService.createRetrofitService
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.factory.RetrofitTestService.setup
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
import java.io.File
import java.util.concurrent.TimeUnit

class RemoteRepositoryTest : RemoteRepository(), TestSteps {

    @PublishedApi
    internal lateinit var mockWebServer: MockWebServer
    private lateinit var userProfile: UserProfile
    private lateinit var callPath: String

    private val json = "{\n" +
            "  \"total_count\": 156,\n" +
            "  \"incomplete_results\": false,\n" +
            "  \"items\": [\n" +
            "    {\n" +
            "      \"login\": \"torvalds\",\n" +
            "      \"id\": 1024025,\n" +
            "      \"node_id\": \"MDQ6VXNlcjEwMjQwMjU=\",\n" +
            "      \"avatar_url\": \"https://avatars0.githubusercontent.com/u/1024025?v=4\",\n" +
            "      \"gravatar_id\": \"\",\n" +
            "      \"url\": \"https://api.github.com/users/torvalds\",\n" +
            "      \"html_url\": \"https://github.com/torvalds\",\n" +
            "      \"followers_url\": \"https://api.github.com/users/torvalds/followers\",\n" +
            "      \"following_url\": \"https://api.github.com/users/torvalds/following{/other_user}\",\n" +
            "      \"gists_url\": \"https://api.github.com/users/torvalds/gists{/gist_id}\",\n" +
            "      \"starred_url\": \"https://api.github.com/users/torvalds/starred{/owner}{/repo}\",\n" +
            "      \"subscriptions_url\": \"https://api.github.com/users/torvalds/subscriptions\",\n" +
            "      \"organizations_url\": \"https://api.github.com/users/torvalds/orgs\",\n" +
            "      \"repos_url\": \"https://api.github.com/users/torvalds/repos\",\n" +
            "      \"events_url\": \"https://api.github.com/users/torvalds/events{/privacy}\",\n" +
            "      \"received_events_url\": \"https://api.github.com/users/torvalds/received_events\",\n" +
            "      \"type\": \"User\",\n" +
            "      \"site_admin\": false,\n" +
            "      \"score\": 1624.8047\n" +
            "    }\n" +
            "  ]\n" +
            "}"

    @UnstableDefault
    @BeforeEach
    override fun setupDependencies() {
        mockWebServer = setup()
        userProfile = createRetrofitService()
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
                        json
                    )
            )
        }

        doWhen {
            runBlocking {
                val userProfile = userProfile.provideGithubUsersListAsync("torvalds", 1, 20)
                response = callApi { userProfile }
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
                response = callApi { userProfile }
            }
        }

        then {
            var isSuccess = false
            if (response is APICallResult.Success<*>) isSuccess = true

            assertEquals(true, isSuccess)
            assertEquals(1, ((response as APICallResult.Success<Int>).data))
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
                response = callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(7, ((response as APICallResult.Error).error))
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
                response = callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(9, ((response as APICallResult.Error).error))
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
                response = callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(8, ((response as APICallResult.Error).error))
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
                response = callApi { userProfile }
            }
        }

        then {
            var isSuccessful = false
            if (response is APICallResult.Error) isSuccessful = false

            assertEquals(false, isSuccessful)
            assertEquals(10, ((response as APICallResult.Error).error))
        }
    }

    @AfterEach
    fun teardown() {
        mockWebServer.shutdown()
    }

    private fun getJson(path: String): String {

        this.javaClass.classLoader?.getResource(path)?.path?.let {
            callPath = it
        }

        // Load the JSON response
        val file = File(callPath)
        return String(file.readBytes())
    }
}