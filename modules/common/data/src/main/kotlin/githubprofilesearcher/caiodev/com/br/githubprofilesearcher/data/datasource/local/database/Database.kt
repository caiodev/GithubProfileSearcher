package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.datasource.local.database

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.data.feature.profile.repository.local.database.dao.Profile

fun interface Database {
    fun profile(): Profile
}
