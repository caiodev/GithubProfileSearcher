package githubprofilesearcher.caiodev.com.br.githubprofilesearcher

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.di.global
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.di.userProfileViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@OptIn(ExperimentalSerializationApi::class)
class App : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(global, userProfileViewModel)
        }
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve(coilCacheDir))
                    .maxSizePercent(diskCacheCap)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(applicationContext)
                    .maxSizePercent(memoryCacheCap)
                    .build()
            }
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .build()
    }

    companion object {
        private const val coilCacheDir = "image_cache"
        private const val diskCacheCap = 0.10
        private const val memoryCacheCap = 0.25
    }
}
