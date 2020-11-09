package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants

object Constants {

    // Intent id
    const val githubProfileUrl = "githubProfileUrl"

    // RecyclerView espresso.view types
    const val empty = 0

    // Subtypes of Generic
    const val loading = 2
    const val retry = 3
    const val endOfResults = 4
    const val header = 5

    // Call states
    // Success
    const val successWithoutBody = 1

    // Error
    // Exceptions
    const val connectException = 2
    const val genericException = 3
    const val socketTimeoutException = 4
    const val sslHandshakeException = 5
    const val unknownHostException = 6

    // HTTP codes
    const val clientSideError = 7
    const val serverSideError = 8
    const val forbidden = 9

    // Generic
    const val genericError = 10

    // Pagination
    const val numberOfItemsPerPage = 20

    // Adapter positions
    // Header
    const val headerAdapter = 0
    const val githubProfileAdapter = 1
    const val transientViewsAdapter = 2

    // Database name
    const val appDb = "app-db"

    // HTTP error codes
    const val fourHundred = 400
    const val fourHundredAndTwo = 402
    const val fourHundredAndThree = 403
    const val fourHundredAndFour = 404
    const val fourHundredAndNinetyNine = 499
    const val fiveHundred = 500
    const val fiveHundredAndNinetyEight = 598

    // Others
    const val emptyString = ""
    const val https = "https"
    const val twenty = 20
}
