package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R.string
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.cast.ValueCasting
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.cell.handleResult
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.UserModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.features.profile.model.local.repository.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.states.Success
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.mapper.mapFrom
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.state.ProfileState
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.datasources.local.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.datasources.remote.IProfileOriginRepository

internal class ProfileDataObtainmentCell(
    private val keyValueRepository: IKeyValueRepository,
    private val userDatabaseRepository: IUserDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : IProfileDataObtainmentCell {
    private var profileName: String = emptyString()
    private var pageNumber = INITIAL_PAGE
    private val userList = arrayListOf<User>()

    override suspend fun obtainProfileDataList(
        profile: String,
        shouldListBeCleared: Boolean,
    ): ProfileState {
        keyValueRepository.setValue(
            key = ProfileKeyValueIDs.CurrentProfileText,
            value = profile,
        )

        if (profile.isNotEmpty()) profileName = profile
        pageNumber = if (shouldListBeCleared) INITIAL_PAGE else ++pageNumber

        val value =
            profileOriginRepository.fetchProfileInfo(
                user = profileName,
                pageNumber = pageNumber,
            )

        return handleResult<ProfileState>(
            value = value,
            onSuccess = { success ->
                onUserInfoArrivalSuccess(
                    state = success,
                    shouldListBeCleared = shouldListBeCleared,
                )
            },
            onFailure = { error ->
                ProfileState(errorMessage = error.error)
            },
        )
    }

    private suspend fun onUserInfoArrivalSuccess(
        state: State<*>,
        shouldListBeCleared: Boolean,
    ): ProfileState {
        val userModel = ValueCasting.castTo<Success<UserModel>>(state)?.data

        userModel?.let {
            return if (it.profileList.isNotEmpty()) {
                if (shouldListBeCleared) {
                    userList.clear()
                    userList.addAll(it.profileList)
                } else {
                    userList.addAll(it.profileList)
                }

                executePostUserInfoArrival(shouldListBeCleared = shouldListBeCleared)

                ProfileState(
                    isSuccess = true,
                    isSuccessWithContent = true,
                    content = userList.mapFrom(),
                )
            } else {
                ProfileState(
                    errorMessage = string.client_side,
                    areAllResultsEmpty = userList.isEmpty(),
                )
            }
        } ?: run {
            return ProfileState(errorMessage = string.generic)
        }
    }

    private suspend fun executePostUserInfoArrival(shouldListBeCleared: Boolean) {
        if (shouldListBeCleared) userDatabaseRepository.drop()

        userDatabaseRepository.upsert(profileList = userList.toList())

        keyValueRepository.setValue(
            key = ProfileKeyValueIDs.CurrentProfileText,
            value = emptyString(),
        )

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
