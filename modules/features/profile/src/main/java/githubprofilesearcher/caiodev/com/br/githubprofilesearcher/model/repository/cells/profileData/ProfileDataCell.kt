package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.cells.profileData

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
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.aggregator.ProfileDataCellAggregator.Companion.INITIAL_PAGE
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.repository.IProfileOriginRepository

class ProfileDataCell(
    private val keyValueRepository: IKeyValueRepository,
    private val profileDatabaseRepository: IProfileDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : IProfileDataCell {
    override suspend fun obtainProfileDataList(
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
                executeOnProvidedUserInformation(
                    value = value,
                )
            },
        )
    }

    private suspend fun executeOnProvidedUserInformation(value: State<*>) {
        keyValueRepository.setValue(key = ProfileKeyValueIDs.CurrentProfileText, value = obtainDefaultString())

        if (!keyValueRepository.getValue<Boolean>(ProfileKeyValueIDs.SuccessStatus)) {
            keyValueRepository.setValue(key = ProfileKeyValueIDs.SuccessStatus, value = true)
        }

        val data = ValueCasting.castTo<SuccessWithBody<Profile>>(value)?.data

        data?.let {
            if (it.profile.isNotEmpty()) {
                profileDatabaseRepository.dropProfileInformation()
                profileDatabaseRepository.insertProfilesIntoDb(profileList = it.profile)
                saveDataAfterSuccess()
            } else {
                handleError(Generic)
            }
        }
    }

    private suspend fun saveDataAfterSuccess() {
        with(keyValueRepository) {
            setValue(
                key = ProfileKeyValueIDs.CurrentProfileText,
                value = getValue<String>(key = ProfileKeyValueIDs.TemporaryProfileText),
            )
            setValue(key = ProfileKeyValueIDs.LastAttemptStatus, value = false)
            setValue(key = ProfileKeyValueIDs.CallStatus, value = false)

            if (getValue(key = ProfileKeyValueIDs.DeletedProfileStatus) &&
                getValue<String>(key = ProfileKeyValueIDs.ProfileText).isNotEmpty()
            ) {
                setValue(key = ProfileKeyValueIDs.SearchStatus, value = true)
            } else {
                setValue(key = ProfileKeyValueIDs.SearchStatus, value = false)
            }

            setValue(
                key = ProfileKeyValueIDs.PageNumber,
                getValue<Int>(key = ProfileKeyValueIDs.PageNumber).plus(
                    INITIAL_PAGE,
                ),
            )
        }
    }
}
