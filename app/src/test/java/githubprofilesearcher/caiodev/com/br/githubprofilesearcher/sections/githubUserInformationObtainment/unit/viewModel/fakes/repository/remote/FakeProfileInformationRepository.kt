package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.unit.viewModel.fakes.repository.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.GenericProfileRepository

class FakeProfileInformationRepository :
    GenericProfileRepository {

    private val fakeRemoteRepository =
        FakeRemoteRepository()

    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) = fakeRemoteRepository.provideFakeResponse()
}
