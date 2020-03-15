package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.githubUserInformationObtainment.viewModel

class GithubProfileViewModelFlags {

    //Information cache variables
    var temporaryCurrentProfile: String = ""
    var currentProfile: String = ""
    var lastVisibleItem: Int = 0
    var pageNumber: Int = 1

    //Call related flags
    var hasFirstSuccessfulCallBeenMade: Boolean = false
    var isThereAnOngoingCall: Boolean = false
    var hasUserTriggeredANewRequest: Boolean = false

    var hasAnyUserRequestedUpdatedData: Boolean = false
    var shouldASearchBePerformed: Boolean = true

    var isThereAnyProfileToBeSearched: Boolean = false
    var isTheNumberOfItemsOfTheLastCallLessThanTwenty: Boolean = false

    //Transient list item view flags
    var isEndOfResultsItemVisible: Boolean = false
    var isPaginationLoadingItemVisible: Boolean = false
    var isRetryItemVisible: Boolean = false
}