package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.profile.model.repository.local.dao.ProfileDao

interface Database {
    fun profileDao(): ProfileDao
}
