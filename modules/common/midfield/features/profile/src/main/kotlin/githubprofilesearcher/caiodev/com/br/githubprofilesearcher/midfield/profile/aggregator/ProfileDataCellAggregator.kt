package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell.IProfileDataObtainmentCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.state.ProfileState

class ProfileDataCellAggregator(
    private val keyValueRepository: IKeyValueRepository,
    private val profileDataCell: IProfileDataObtainmentCell,
) : IProfileDataCellAggregator {
    override suspend fun <T> getValue(key: Enum<*>): T = keyValueRepository.getValue(key = key)

    override suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) {
        keyValueRepository.setValue(key = key, value = value)
    }

    override suspend fun obtainProfileDataList(
        profile: String,
        shouldListBeCleared: Boolean,
        isCoroutineActive: () -> Boolean,
    ): ProfileState =
        profileDataCell.obtainProfileDataList(
            profile = profile,
            shouldListBeCleared = shouldListBeCleared,
            isCoroutineActive = isCoroutineActive,
        )
}
