package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.network.NetworkChecking
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.db.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.db.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.APIConnector.newInstance
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.rest.repository.remote.RemoteRepository
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

    factory<ILocalRepository> {
        LocalRepository(
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

    single { RemoteRepository() }
}
