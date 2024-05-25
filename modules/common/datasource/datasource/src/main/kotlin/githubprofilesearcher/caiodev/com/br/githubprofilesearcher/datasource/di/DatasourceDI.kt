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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.lazyModule
import kotlin.coroutines.CoroutineContext

val datasource =
    lazyModule {
        single {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME,
            ).build()
        } bind Database::class
        singleOf(::ChuckerInterceptor) bind Interceptor::class
        single { newInstance(interceptor = get()) }
        singleOf(::RemoteFetcher)
        single { Dispatchers.IO } bind CoroutineContext::class
    }
