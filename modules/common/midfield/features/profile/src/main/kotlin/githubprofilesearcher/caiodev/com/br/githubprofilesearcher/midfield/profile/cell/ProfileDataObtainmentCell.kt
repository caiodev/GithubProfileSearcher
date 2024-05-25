package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.cell

import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.R.string
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.core.types.string.emptyString
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.handler.handleResult
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.database.features.profile.entity.UserEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.fetchers.local.keyValue.IKeyValueRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.database.IUserDatabaseRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.local.repository.keyValue.ProfileKeyValueIDs
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.model.UserModel
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.datasource.profile.datasources.remote.repository.IProfileOriginRepository
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.mapper.mapFromEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.mapper.mapToEntity
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.midfield.profile.state.ProfileState

internal class ProfileDataObtainmentCell(
    private val keyValueRepository: IKeyValueRepository,
    private val userDatabaseRepository: IUserDatabaseRepository,
    private val profileOriginRepository: IProfileOriginRepository,
) : IProfileDataObtainmentCell {
    private var profileName: String = emptyString()
    private var pageNumber = INITIAL_PAGE

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
            val profileList = arrayListOf<UserEntity>()
            return if (it.profileList.isNotEmpty()) {
                if (shouldListBeCleared) {
                    userDatabaseRepository.drop()
                    userDatabaseRepository.insert(profileList = it.profileList.mapToEntity())
                } else {
                    userDatabaseRepository.upsert(profileList = it.profileList.mapToEntity())
                }
                profileList.addAll(userDatabaseRepository.get())
                executePostUserInfoArrival()
                ProfileState(
                    isSuccess = true,
                    isSuccessWithContent = true,
                    content = profileList.mapFromEntity(),
                )
            } else {
                ProfileState(
                    errorMessage = string.client_side,
                    areAllResultsEmpty = profileList.isEmpty(),
                )
            }
        } ?: run {
            return ProfileState(
                errorMessage = string.generic,
            )
        }
    }

    private suspend fun executePostUserInfoArrival() {
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
