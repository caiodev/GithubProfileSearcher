package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.repository

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.repository.Repository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.RemoteRepository
import kotlinx.serialization.UnstableDefault

class FakeGithubProfileInformationRepository : Repository {

    private val remoteRepository = RemoteRepository()

    @UnstableDefault
    override suspend fun provideGithubUserInformation(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int
    ) {
    }
//            = remoteRepository.callApi {
//
//    }
}