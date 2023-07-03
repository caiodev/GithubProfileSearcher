package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.remote

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.GenericProfileRepository

class FakeProfileInformationRepository :
    GenericProfileRepository {

    private val fakeRemoteRepository =
        FakeRemoteRepository()

    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ) = fakeRemoteRepository.provideFakeResponse()
}
