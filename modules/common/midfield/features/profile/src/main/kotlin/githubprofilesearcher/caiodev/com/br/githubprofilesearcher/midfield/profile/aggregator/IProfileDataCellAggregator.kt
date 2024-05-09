package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.base.aggregator.contracts.ICellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell.IProfileDataObtainmentCell

interface IProfileDataCellAggregator :
    ICellAggregator,
    IProfileDataObtainmentCell {
    suspend fun <T> getValue(key: Enum<*>): T

    suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    )
}
