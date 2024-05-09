package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.base.cell.contracts.ICell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.state.ProfileState

interface IProfileDataObtainmentCell : ICell {
    suspend fun obtainProfileDataList(
        profile: String,
        shouldListBeCleared: Boolean,
        isCoroutineActive: () -> Boolean,
    ): ProfileState
}
