package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.init

import android.app.Application
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.BuildConfig
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.sections.githubUserInformationObtainment.model.koinModule.githubProfileInfoObtainmentViewModel
import kotlinx.serialization.UnstableDefault
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@Suppress("UNUSED")
class App : Application() {

    @UnstableDefault
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(githubProfileInfoObtainmentViewModel)
        }
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}