package githubprofilesearcher.caiodev.com.br.githubprofilesearcher

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.extensions.runTaskOnBackground
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.models.profile.UserProfile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.viewModel.fakes.repository.local.FakeLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.unit.viewModel.fakes.repository.remote.FakeProfileInformationRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.TestSteps
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.testing.coroutines.junit5.CoroutinesTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import utils.base.liveData.junit4.LiveDataTestUtil
import utils.base.liveData.junit5.LiveDataTestExtension
import java.io.Writer

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class, LiveDataTestExtension::class)
class ProfileViewModelTest : TestSteps {

    private lateinit var viewModel: githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel

    @BeforeEach
    override fun setupDependencies() {
        viewModel =
            githubprofilesearcher.caiodev.com.br.githubprofilesearcher.viewModel.ProfileViewModel(
                FakeLocalRepository(),
                FakeProfileInformationRepository(),
            )
    }

    @Test
    fun requestUpdatedGithubProfiles() = runBlocking {
        var githubInfo: UserProfile? = null

        given {
            viewModel.requestUpdatedProfiles()
        }

        doWhen {
            githubInfo =
                LiveDataTestUtil.getValue(viewModel.successStateFlow).first()
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
