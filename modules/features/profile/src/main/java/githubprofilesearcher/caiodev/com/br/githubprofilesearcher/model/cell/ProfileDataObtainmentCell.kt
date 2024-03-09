package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.base.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.aggregator.extension.handleResult
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.UserModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.mapper.mapFrom
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.remote.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.state.ProfileState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R as Core

internal class ProfileDataObtainmentCell(
    private val keyValueRepository: IKeyValueRepository,
    private val userDatabaseRepository: IUserDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : IProfileDataObtainmentCell {

    private var profileName: String = emptyString()
    private var pageNumber = INITIAL_PAGE
    private val userList = arrayListOf<User>()
    private var profileState = ProfileState()

    override suspend fun obtainProfileDataList(
        profile: String,
        shouldListBeCleared: Boolean
    ): ProfileState {
        keyValueRepository.setValue(
            key = ProfileKeyValueIDs.CurrentProfileText,
            value = profile,
        )

        if (profile.isNotEmpty()) profileName = profile
        pageNumber = if (shouldListBeCleared) INITIAL_PAGE else pageNumber.plus(INITIAL_PAGE)

        val value = profileOriginRepository.fetchProfileInfo(
            user = profileName,
            pageNumber = pageNumber,
        )

        handleResult(
            value = value,
            onSuccess = { content ->
                onUserInfoArrivalSuccess(
                    state = content,
                    shouldListBeCleared = shouldListBeCleared,
                )
            },
            onFailure = { error ->
                profileState = ProfileState(errorMessage = error.error)
            }
        )
        return profileState
    }

    private suspend fun onUserInfoArrivalSuccess(
        state: State<*>,
        shouldListBeCleared: Boolean,
    ) {
        val userModel = ValueCasting.castTo<Success<UserModel>>(state)?.data

        userModel?.let {
            if (it.profileList.isNotEmpty()) {
                if (shouldListBeCleared) {
                    userList.clear()
                    userList.addAll(it.profileList)
                } else {
                    userList.addAll(it.profileList)
                }

                onUserInfoArrival()

                profileState = ProfileState(
                    isSuccess = true,
                    isSuccessWithContent = true,
                    content = userList.mapFrom(),
                )
            } else {
                profileState = ProfileState(
                    errorMessage = Core.string.client_side,
                    areAllResultsEmpty = userList.isEmpty()
                )
            }
        } ?: run {
            profileState = ProfileState(errorMessage = Core.string.generic)
        }
    }

    private suspend fun onUserInfoArrival() {
        userDatabaseRepository.dropUserInfo()
        userDatabaseRepository.insertUsers(profileList = userList.toList())

        keyValueRepository.setValue(key = ProfileKeyValueIDs.CurrentProfileText, value = emptyString())

        if (!keyValueRepository.getValue<Boolean>(ProfileKeyValueIDs.SuccessStatus)) {
            keyValueRepository.setValue(key = ProfileKeyValueIDs.SuccessStatus, value = true)
        }

        keyValueRepository.setValue(key = ProfileKeyValueIDs.LastAttemptStatus, value = false)
        keyValueRepository.setValue(key = ProfileKeyValueIDs.CallStatus, value = false)

        if (keyValueRepository.getValue(key = ProfileKeyValueIDs.DeletedProfileStatus) &&
            keyValueRepository.getValue<String>(key = ProfileKeyValueIDs.ProfileText).isNotEmpty()
        ) {
            keyValueRepository.setValue(key = ProfileKeyValueIDs.SearchStatus, value = true)
        } else {
            keyValueRepository.setValue(key = ProfileKeyValueIDs.SearchStatus, value = false)
        }
    }

    companion object {
        const val INITIAL_PAGE = 1
    }
}
