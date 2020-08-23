package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel.fakes.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericGithubProfileRepository


class FakeGithubProfileInformationRepository :
    GenericGithubProfileRepository {

    private val fakeRemoteRepository =
        FakeRemoteRepository()

    
    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = fakeRemoteRepository.provideFakeResponse()
}
