package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.ICell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.state.ProfileState

interface IProfileDataObtainmentCell : ICell {
    suspend fun obtainProfileDataList(
        profile: String,
        shouldListBeCleared: Boolean,
    ): ProfileState
}
