package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.di

import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.api.SourceConnector.newInstance
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@ExperimentalSerializationApi
val global = module {

    single<Database> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.databaseName,
        ).build()
    }

    single { newInstance() }

    single { RemoteFetcher() }
}
