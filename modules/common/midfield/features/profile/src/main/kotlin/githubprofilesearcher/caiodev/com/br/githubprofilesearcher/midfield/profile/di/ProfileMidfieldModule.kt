package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.di.profileDatasourceModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator.IProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator.ProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell.IProfileDataObtainmentCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell.ProfileDataObtainmentCell
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.includes
import org.koin.dsl.bind
import org.koin.dsl.lazyModule

val profileMidfieldModule =
    lazyModule {
        includes(profileDatasourceModule)
        factoryOf(::ProfileDataObtainmentCell) bind IProfileDataObtainmentCell::class
        factoryOf(::ProfileDataCellAggregator) bind IProfileDataCellAggregator::class
    }
