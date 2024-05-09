package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue

interface IKeyValueRepository {
    suspend fun <T> getValue(key: Enum<*>): T

    suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    )
}
