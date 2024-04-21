package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.contracts.ICellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.cell.IProfileDataObtainmentCell

interface IProfileDataCellAggregator :
    ICellAggregator,
    IKeyValueRepository,
    IProfileDataObtainmentCell
