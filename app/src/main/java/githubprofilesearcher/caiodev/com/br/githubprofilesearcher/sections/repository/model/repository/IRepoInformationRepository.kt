package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.repository

interface IRepoInformationRepository {
    suspend fun fetchUser(user: String = ""): Any
    suspend fun fetchRepository(user: String = ""): Any
}
