package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.interfaces

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.repository.local.dao.GithubProfilesDao

interface Database {
    fun githubProfilesDao(): GithubProfilesDao
}
