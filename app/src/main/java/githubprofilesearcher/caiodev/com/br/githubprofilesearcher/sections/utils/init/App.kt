package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.init

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.BuildConfig
import timber.log.Timber

@Suppress("UNUSED")
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
            Timber.plant(Timber.DebugTree())
        }
    }
}