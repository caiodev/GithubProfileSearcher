package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.cells.profileData.IProfileDataCell

class ProfileDataCellAggregator(
    private val keyValueRepository: IKeyValueRepository,
    private val profileDataCell: IProfileDataCell,
) : IProfileDataCellAggregator {
    override suspend fun <T> getValue(key: Enum<*>): T = keyValueRepository.getValue(key = key)

    override suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) = keyValueRepository.setValue(key = key, value = value)

    override suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*> =
        profileDataCell.obtainProfileDataList(
            user = user,
            pageNumber = pageNumber,
            maxResultsPerPage = maxResultsPerPage,
        )

    internal companion object {
        const val INITIAL_PAGE = 1
        const val ITEMS_PER_PAGE = 20
    }
}
