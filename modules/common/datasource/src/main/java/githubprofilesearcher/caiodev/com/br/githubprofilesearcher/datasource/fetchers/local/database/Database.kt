package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao

fun interface Database {
    fun profileDao(): ProfileDao
}
