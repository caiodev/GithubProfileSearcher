package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.app

import android.app.Application
import androidx.appcompat.content.res.AppCompatResources
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.profile.di.profileModule
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.resources.R as Resources

@OptIn(KoinExperimentalAPI::class)
class App :
    Application(),
    KoinStartup,
    SingletonImageLoader.Factory {
    override fun onKoinStartup(): KoinAppDeclaration =
        {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            lazyModules(profileModule)
        }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val defaultImage =
            AppCompatResources
                .getDrawable(
                    applicationContext,
                    Resources.mipmap.ic_launcher,
                )
                ?.asImage()
        return ImageLoader
            .Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .diskCache {
                DiskCache
                    .Builder()
                    .directory(cacheDir.resolve(COIL_CACHE_DIR).toOkioPath())
                    .maxSizePercent(DISK_CACHE_CAP)
                    .build()
            }
            .memoryCache {
                MemoryCache
                    .Builder()
                    .maxSizePercent(context, MEMORY_CACHE_CAP)
                    .build()
            }
            .placeholder(defaultImage)
            .error(defaultImage)
            .logger(DebugLogger())
            .build()
    }

    companion object {
        private const val COIL_CACHE_DIR = "coil_image"
        private const val DISK_CACHE_CAP = .02
        private const val MEMORY_CACHE_CAP = .05
    }
}
