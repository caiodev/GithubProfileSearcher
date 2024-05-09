package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.di

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.di.profileDatasourceModule
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator.IProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.aggregator.ProfileDataCellAggregator
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell.IProfileDataObtainmentCell
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell.ProfileDataObtainmentCell
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val profileMidfieldModule = module {
    loadKoinModules(githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.di.profileDatasourceModule)
    factory<IProfileDataObtainmentCell> {
        ProfileDataObtainmentCell(
            keyValueRepository = get(),
            userDatabaseRepository = get(),
            profileOriginRepository = get(),
        )
    }

    factory<IProfileDataCellAggregator> {
        ProfileDataCellAggregator(
            keyValueRepository = get(),
            profileDataCell = get(),
        )
    }
}
