package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.init

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.BuildConfig
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.R
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.di.userProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.repository.model.diModules.userRepositoryViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.di.globalModule
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("Unused")
class App : Application(), ImageLoaderFactory {
    @OptIn(ExperimentalSerializationApi::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(globalModule, userProfileViewModel, userRepositoryViewModel)
        }
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .placeholder(R.mipmap.ic_launcher)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .build()
            }
            .error(R.mipmap.ic_launcher)
            .build()
    }
}
