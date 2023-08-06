package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.Aggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileOriginRepository

interface IProfileDataAggregator : Aggregator, IKeyValueRepository, IProfileOriginRepository
