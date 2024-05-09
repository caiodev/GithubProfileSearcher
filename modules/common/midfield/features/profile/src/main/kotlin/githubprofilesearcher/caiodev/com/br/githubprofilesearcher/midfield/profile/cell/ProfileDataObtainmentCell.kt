package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R.string
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.handler.handleResult
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.model.User
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.model.UserModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.mapper.mapFromRemote
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.mapper.mapToEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.state.ProfileState

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
        isCoroutineActive: () -> Boolean,
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
                isCoroutineActive = isCoroutineActive,
            )

        return value.handleResult<UserModel, ProfileState>(
            onSuccess = { success ->
                onUserInfoArrivalSuccess(
                    userModel = success,
                    shouldListBeCleared = shouldListBeCleared,
                )
            },
            onFailure = { error ->
                ProfileState(
                    errorMessage = error.error,
                )
            },
        )
    }

    private suspend fun onUserInfoArrivalSuccess(
        userModel: UserModel?,
        shouldListBeCleared: Boolean,
    ): ProfileState {
        userModel?.let {
            return if (it.profileList.isNotEmpty()) {
                if (shouldListBeCleared) userList.clear()
                userList.addAll(it.profileList)
                executePostUserInfoArrival(shouldListBeCleared = shouldListBeCleared)
                ProfileState(
                    isSuccess = true,
                    isSuccessWithContent = true,
                    content = userList.mapFromRemote(),
                )
            } else {
                ProfileState(
                    errorMessage = string.client_side,
                    areAllResultsEmpty = userList.isEmpty(),
                )
            }
        } ?: run {
            return ProfileState(
                errorMessage = string.generic,
            )
        }
    }

    private suspend fun executePostUserInfoArrival(shouldListBeCleared: Boolean) {
        if (shouldListBeCleared) userDatabaseRepository.drop()

        userDatabaseRepository.upsert(profileList = userList.mapToEntity())

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
