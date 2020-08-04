package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.fakes.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository
import kotlinx.serialization.UnstableDefault

class FakeGithubProfileInformationRepository :
    GenericGithubProfileRepository {

    private val fakeRemoteRepository =
        FakeRemoteRepository()

    @UnstableDefault
    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = fakeRemoteRepository.provideFakeResponse()
}