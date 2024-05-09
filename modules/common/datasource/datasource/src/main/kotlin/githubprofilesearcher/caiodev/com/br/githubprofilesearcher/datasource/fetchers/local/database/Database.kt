package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.dao.User

fun interface Database {
    fun user(): User
}
