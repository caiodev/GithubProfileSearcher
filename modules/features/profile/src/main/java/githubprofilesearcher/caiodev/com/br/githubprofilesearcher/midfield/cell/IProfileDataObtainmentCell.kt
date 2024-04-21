package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.cell.contracts.ICell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.state.ProfileState

interface IProfileDataObtainmentCell : ICell {
    suspend fun obtainProfileDataList(
        profile: String,
        shouldListBeCleared: Boolean,
    ): ProfileState
}
