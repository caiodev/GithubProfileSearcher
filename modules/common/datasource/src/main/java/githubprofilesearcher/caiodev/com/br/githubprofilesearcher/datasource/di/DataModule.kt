package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.network.NetworkChecking
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.ILocalFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.LocalFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.api.APIConnector.newInstance
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

    factory<ILocalFetcher> {
        LocalFetcher(
            get(),
            get(),
        )
    }

    single {
        NetworkChecking(
            androidContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
        )
    }

    single { newInstance() }

    single { RemoteFetcher() }
}
