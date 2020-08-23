package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.diModules

import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val globalModule = module {
    single<Database> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, Constants.appDb
        ).build()
    }
}
