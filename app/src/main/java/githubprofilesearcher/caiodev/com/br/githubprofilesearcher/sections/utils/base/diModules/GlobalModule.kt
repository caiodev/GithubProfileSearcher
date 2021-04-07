package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.diModules

import androidx.room.Room
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.Database
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces.GenericLocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.LocalRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.db.AppDatabase
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.remote.RemoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val globalModule = module {
    single<Database> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.databaseName
        ).build()
    }

    factory<GenericLocalRepository> {
        LocalRepository(
            get(),
            get()
        )
    }

    single {
        RemoteRepository()
    }
}
