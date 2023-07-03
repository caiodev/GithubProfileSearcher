package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.repository.local.db

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.ProfileDao

interface Database {
    fun profileDao(): ProfileDao
}
