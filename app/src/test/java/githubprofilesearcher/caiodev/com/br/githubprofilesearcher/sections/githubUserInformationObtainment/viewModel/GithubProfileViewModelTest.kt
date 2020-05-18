package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.async.CoroutinesTestExtension
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.async.LiveDataTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.base.TestSteps
import utils.base.liveData.LiveDataTestUtil

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class, LiveDataTestExtension::class)
class GithubProfileViewModelTest : TestSteps {

    private lateinit var viewModel: GithubProfileViewModel

    @ExperimentalCoroutinesApi
    @BeforeEach
    override fun setupDependencies() {
        viewModel =
            GithubProfileViewModel(SavedStateHandle(), FakeGithubProfileInformationRepository())
    }

    @UnstableDefault
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
            assertThat(githubInfo?.login).isEqualTo("torvalds")
        }
    }

    @Test
    fun requestMoreGithubProfiles() = runBlocking {

        given {

        }

        doWhen {

        }

        then {

        }
    }

    @UnstableDefault
    @Test
    fun provideProfileUrlThroughViewModel() = runBlocking {

        doWhen {
            viewModel.requestUpdatedGithubProfiles()
        }

        then {
            assertThat(viewModel.provideProfileUrlThroughViewModel(0)).isEqualTo("https://github.com/torvalds")
        }
    }
}