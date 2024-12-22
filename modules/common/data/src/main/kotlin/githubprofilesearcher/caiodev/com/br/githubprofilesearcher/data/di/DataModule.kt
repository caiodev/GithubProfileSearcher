package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.di

import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.local.database.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.local.database.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.remote.api.newInstance
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.remote.fetcher.RemoteFetcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.lazyModule
import kotlin.coroutines.CoroutineContext

internal val dataModule =
    lazyModule {
        single {
            Room
                .databaseBuilder(
                    androidContext(),
                    AppDatabase::class.java,
                    AppDatabase.DATABASE_NAME,
                )
                .build()
        } bind Database::class
        singleOf(::ChuckerInterceptor) bind Interceptor::class
        single { newInstance(interceptor = get()) }
        singleOf(::RemoteFetcher)
        single { Dispatchers.IO } bind CoroutineContext::class
    }
