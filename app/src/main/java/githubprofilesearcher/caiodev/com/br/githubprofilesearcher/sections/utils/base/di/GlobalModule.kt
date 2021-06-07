package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.di

import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.rest.APIConnector.createAPIConnectorInstance
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@ExperimentalSerializationApi
val globalModule = module {
    single<Database> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.databaseName
        ).build()
    }

    factory<ILocalRepository> {
        LocalRepository(
            get(),
            get()
        )
    }

    single {
        createAPIConnectorInstance()
    }

    single {
        RemoteRepository()
    }
}
