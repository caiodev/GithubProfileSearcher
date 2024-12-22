package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.domain.repository.keyValue

interface IKeyValueRepository {
    suspend fun <T> getValue(key: Enum<*>): T

    suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    )
}
