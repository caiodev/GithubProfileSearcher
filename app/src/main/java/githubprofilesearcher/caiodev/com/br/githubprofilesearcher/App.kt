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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.ui.R as UI

@OptIn(ExperimentalSerializationApi::class)
class App : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(global, userProfileViewModel)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve(COIL_CACHE_DIR))
                    .maxSizePercent(DISK_CACHE_CAP)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(applicationContext)
                    .maxSizePercent(MEMORY_CACHE_CAP)
                    .build()
            }
            .placeholder(UI.mipmap.ic_launcher)
            .error(UI.mipmap.ic_launcher)
            .build()
    }

    companion object {
        private const val COIL_CACHE_DIR = "image_cache"
        private const val DISK_CACHE_CAP = 0.10
        private const val MEMORY_CACHE_CAP = 0.25
    }
}
