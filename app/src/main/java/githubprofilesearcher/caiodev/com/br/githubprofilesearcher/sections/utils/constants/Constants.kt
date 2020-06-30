package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants

import android.os.Bundle
import androidx.core.os.bundleOf
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.model.viewTypes.GithubProfileInformation
import okhttp3.MediaType.Companion.toMediaType

object Constants {

    //Intent id
    const val githubProfileUrl = "githubProfileUrl"

    //RecyclerView view types
    const val empty = 0

    //Subtypes of Generic
    const val loading = 2
    const val retry = 3
    const val endOfResults = 4
    const val header = 5

    //Call states
    //Success
    const val successWithoutBody = 1

    //Error
    //Exceptions
    const val connectException = 2
    const val genericException = 3
    const val socketTimeoutException = 4
    const val sslHandshakeException = 5
    const val unknownHostException = 6

    //HTTP codes
    const val clientSideError = 7
    const val serverSideError = 8
    const val forbidden = 9

    //Generic
    const val genericError = 10

    //Pagination
    const val numberOfItemsPerPage = 20

    //Adapter positions
    //Header
    const val headerAdapter = 0
    const val githubProfileAdapter = 1
    const val transientViewsAdapter = 2

    //HandleSavedState properties
    //Layout related
    const val hasUserDeletedProfileText = "hasUserDeletedProfileText"
    const val textInputEditTextProfile = "textInputEditTextProfile"
    const val shouldRecyclerViewAnimationBeExecuted = "shouldRecyclerViewAnimationBeExecuted"

    //Information cache variables
    const val temporaryCurrentProfile = "temporaryCurrentProfile"
    const val currentProfile = "currentProfile"
    const val pageNumber = "pageNumber"

    //Call related
    const val hasASuccessfulCallAlreadyBeenMade = "hasASuccessfulCallAlreadyBeenMade"
    const val hasLastCallBeenUnsuccessful = "hasLastCallBeenUnsuccessful"
    const val isThereAnOngoingCall = "isThereAnOngoingCall"
    const val hasUserRequestedUpdatedData = "hasAnyUserRequestedUpdatedData"
    const val shouldASearchBePerformed = "shouldASearchBePerformed"
    const val isTextInputEditTextEmpty = "isTextInputEditTextEmpty"
    const val numberOfItems = "numberOfItems"
    const val githubProfilesList = "githubProfilesList"

    //Header View
    const val isHeaderVisible = "isHeaderItemVisible"

    //Transient Views
    const val isEndOfResultsViewVisible = "isEndOfResultsItemVisible"
    const val isPaginationLoadingViewVisible = "isPaginationLoadingItemVisible"
    const val isRetryViewVisible = "isRetryItemVisible"

    //Retrofit related
    const val baseUrl = "https://api.github.com/"
    const val timberTag = "OkHttp"
    val mediaType = "application/json".toMediaType()
    const val retrofitTimeout = 60L

    //Chrome packages
    const val stable = "com.android.chrome"
    const val beta = "com.chrome.beta"
    const val dev = "com.chrome.dev"
    const val local = "com.google.android.apps.chrome"

    //Others
    const val emptyString = ""
    const val https = "https"

    val savedStateHandleArguments: Bundle = bundleOf(
        hasUserDeletedProfileText to false,
        textInputEditTextProfile to emptyString,
        shouldRecyclerViewAnimationBeExecuted to true,
        temporaryCurrentProfile to emptyString,
        currentProfile to emptyString,
        pageNumber to 1,
        hasASuccessfulCallAlreadyBeenMade to false,
        hasLastCallBeenUnsuccessful to false,
        isThereAnOngoingCall to false,
        hasUserRequestedUpdatedData to false,
        shouldASearchBePerformed to true,
        isTextInputEditTextEmpty to true,
        numberOfItems to 0,
        githubProfilesList to listOf<GithubProfileInformation>(),
        isHeaderVisible to false,
        isEndOfResultsViewVisible to false,
        isPaginationLoadingViewVisible to false,
        isRetryViewVisible to false
    )
}