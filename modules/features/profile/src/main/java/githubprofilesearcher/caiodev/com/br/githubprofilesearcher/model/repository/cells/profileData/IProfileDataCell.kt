package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.cells.profileData

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.ICell

interface IProfileDataCell : ICell {
    suspend fun obtainProfileDataList(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*>
}
