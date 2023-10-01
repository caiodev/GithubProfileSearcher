package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.SuccessWithBody
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.obtainDefaultString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.extension.handleError
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.extension.handleResult
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.Profile
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.IProfileDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.IProfileOriginRepository

class ProfileDataAggregator(
    private val keyValueRepository: IKeyValueRepository,
    private val profileDatabaseRepository: IProfileDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : IProfileDataAggregator {
    override suspend fun <T> getValue(key: Enum<*>): T {
        return keyValueRepository.getValue(key = key)
    }

    override suspend fun <T> setValue(
        key: Enum<*>,
        value: T,
    ) {
        keyValueRepository.setValue(key = key, value = value)
    }

    override suspend fun fetchProfileInfo(
        user: String,
        pageNumber: Int,
        maxResultsPerPage: Int,
    ): State<*> {
        val value =
            profileOriginRepository.fetchProfileInfo(
                user = user,
                pageNumber = pageNumber,
                maxResultsPerPage = maxResultsPerPage,
            )

        return handleResult(
            value = value,
            onSuccess = {
                executeOnProvideUserInformation(value)
            },
        )
    }

    private suspend fun executeOnProvideUserInformation(value: State<*>) {
        setValue(key = ProfileKeyValueIDs.CurrentProfileText, value = obtainDefaultString())

        if (!getValue<Boolean>(ProfileKeyValueIDs.SuccessStatus)) {
            setValue(key = ProfileKeyValueIDs.SuccessStatus, value = true)
        }

        val list = ValueCasting.castTo<SuccessWithBody<Profile>>(value)?.data

        list?.let {
            if (it.profile.isNotEmpty()) {
                profileDatabaseRepository.dropProfileInformation()
                profileDatabaseRepository.insertProfilesIntoDb(profileList = it.profile)
            } else {
                handleError(Generic)
            }
        }
    }
}
