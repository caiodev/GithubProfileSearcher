package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.di

import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.RemoteFetcher
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.remote.api.newInstance
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val datasource =
    module {
        single<Database> {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).build()
        }
        single<Interceptor> { ChuckerInterceptor(androidContext()) }
        single { newInstance(interceptor = get()) }
        single { RemoteFetcher() }
        single { Dispatchers.IO }
    }
