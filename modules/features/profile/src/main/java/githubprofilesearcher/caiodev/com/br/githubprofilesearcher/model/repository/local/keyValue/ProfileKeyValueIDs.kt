package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.model.repository.local.keyValue

enum class ProfileKeyValueIDs {
    DeletedProfileStatus, //hasUserDeletedProfileText
    ProfileText, //profile
    TemporaryProfileText, //temporaryCurrentProfile
    CurrentProfileText, //currentProfile
    PageNumber, //pageNumber
    SuccessStatus, //hasASuccessfulCallAlreadyBeenMade
    LastAttemptStatus, //hasLastCallBeenUnsuccessful
    CallStatus, //isThereAnOngoingCall
    DataRequestStatus, //hasUserRequestedUpdatedData
    SearchStatus, //shouldASearchBePerformed
    UserInputStatus, //isTextInputEditTextNotEmpty
    HeaderStatus, //isHeaderVisible
    EndOfResultsStatus, //isEndOfResultsViewVisible
    PaginationLoadingStatus, //isPaginationLoadingViewVisible
    RetryStatus, //isRetryViewVisible
    LocalPopulationStatus //isLocalPopulation
}
