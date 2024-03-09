package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.database.UserDao

fun interface Database {
    fun userDao(): UserDao
}
