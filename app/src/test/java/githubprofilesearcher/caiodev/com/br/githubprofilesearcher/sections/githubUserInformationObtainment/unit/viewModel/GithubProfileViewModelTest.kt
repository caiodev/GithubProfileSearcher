package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.GithubProfileInformation
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel.fakes.repository.local.FakeLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel.fakes.repository.remote.FakeGithubProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.GithubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.runTaskOnBackground
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.base.TestSteps
import utils.base.coroutines.junit5.CoroutinesTestExtension
import utils.base.liveData.junit5.LiveDataTestExtension
import utils.base.liveData.junit4.LiveDataTestUtil
import java.io.Writer

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
                LiveDataTestUtil.getValue(viewModel.successLiveData).first()
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

    @Test
    fun runTaskOnBackground_executeAGivenOperationOnBackground() {
        var value = 0

        doWhen {
            runBlocking {
                viewModel.runTaskOnBackground {
                    value = 1
                }
            }
        }

        then {
            assertEquals(1, value)
        }
    }

    @Test
    fun castAttributeThroughViewModel_castTheGivenAttributeToTheGivenType() {
        val text: CharSequence = "blah"

        doWhen {
            viewModel.castValue<String>(text)
        }

        then {
            assertEquals(true, text is String)
            assertEquals(false, text is Writer)
        }
    }
}
