package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.repository.local.datastore.model

data class UserPreferences(
    val hasUserDeletedProfileText: Boolean = false,
    val textInputEditTextProfile: String = "",
    val shouldRecyclerViewAnimationBeExecuted: Boolean = true,
    val temporaryCurrentProfile: String = "",
    val currentProfile: String = "",
    val pageNumber: Int = 0,
    val hasASuccessfulCallAlreadyBeenMade: Boolean = false,
    val hasLastCallBeenUnsuccessful: Boolean = false,
    val isThereAnOngoingCall: Boolean = false,
    val hasUserRequestedUpdatedData: Boolean = false,
    val shouldASearchBePerformed: Boolean = false,
    val isTextInputEditTextEmpty: Boolean = false,
    val numberOfItems: Int = 0,
    val isHeaderVisible: Boolean = false,
    val isEndOfResultsViewVisible: Boolean = false,
    val isPaginationLoadingViewVisible: Boolean = false,
    val isRetryViewVisible: Boolean = false
)
