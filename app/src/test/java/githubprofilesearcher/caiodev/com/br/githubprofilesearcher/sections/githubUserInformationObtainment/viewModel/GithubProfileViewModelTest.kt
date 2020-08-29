package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.fakes.repository.local.FakeLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.fakes.repository.remote.FakeGithubProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.async.CoroutinesTestExtension
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.async.LiveDataTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.base.TestSteps
import utils.base.liveData.LiveDataTestUtil

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class, LiveDataTestExtension::class)
class GithubProfileViewModelTest : TestSteps {

    private lateinit var viewModel: GithubProfileViewModel

    @BeforeEach
    override fun setupDependencies() {
        viewModel =
            GithubProfileViewModel(FakeLocalRepository(), FakeGithubProfileInformationRepository())
    }

    @Test
    fun requestUpdatedGithubProfiles() = runBlocking {
        var githubInfo: GithubProfileInformation? = null

        given {
            viewModel.requestUpdatedGithubProfiles()
        }

        doWhen {
            githubInfo =
                LiveDataTestUtil.getValue(viewModel.successLiveData)[0]
        }

        then {
            assertEquals("torvalds", githubInfo?.login)
        }
    }

    @Test
    fun requestMoreGithubProfiles() = runBlocking {
        given {
            //
        }

        doWhen {
            //
        }

        then {
            //
        }
    }
}
