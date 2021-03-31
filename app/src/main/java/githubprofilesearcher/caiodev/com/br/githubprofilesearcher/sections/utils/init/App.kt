package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.init

import android.app.Application
import androidx.viewbinding.BuildConfig
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.di.githubProfileViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.userRepositoryInformationObtainment.model.diModules.githubUserRepositoryViewModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.diModules.globalModule
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("UNUSED")
class App : Application() {
    @ExperimentalSerializationApi
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(githubProfileViewModel, githubUserRepositoryViewModel, globalModule)
        }
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}
