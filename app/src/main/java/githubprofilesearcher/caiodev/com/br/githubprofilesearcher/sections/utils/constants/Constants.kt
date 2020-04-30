package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants

object Constants {

    //Intent id
    const val githubProfileUrl = "githubProfileUrl"

    //RecyclerView view types
    const val empty = 0
    const val githubProfileCell = 1

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
}