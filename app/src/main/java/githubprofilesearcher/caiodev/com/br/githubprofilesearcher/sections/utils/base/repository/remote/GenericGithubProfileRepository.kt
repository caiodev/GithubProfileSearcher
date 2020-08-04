package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote

import kotlinx.serialization.UnstableDefault

interface GenericGithubProfileRepository {
    
    @UnstableDefault
    suspend fun provideGithubUserInformation(
        user: String = "",
        pageNumber: Int = 0,
        maxResultsPerPage: Int = 0
    ): Any
}