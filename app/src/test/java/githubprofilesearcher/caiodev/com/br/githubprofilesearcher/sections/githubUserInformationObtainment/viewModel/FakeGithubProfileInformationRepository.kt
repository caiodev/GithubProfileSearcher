package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.GenericGithubProfileRepository
import kotlinx.serialization.UnstableDefault

class FakeGithubProfileInformationRepository : GenericGithubProfileRepository {

    private val fakeRemoteRepository = FakeRemoteRepository()

    @UnstableDefault
    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = fakeRemoteRepository.provideFakeResponse()
}