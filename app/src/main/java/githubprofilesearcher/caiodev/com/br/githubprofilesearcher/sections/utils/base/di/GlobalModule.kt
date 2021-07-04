package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.ILocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network.NetworkChecking
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.rest.APIConnector.newInstance
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
        NetworkChecking(androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    }

    single {
        newInstance()
    }

    single {
        RemoteRepository()
    }
}
