package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.constants

object Constants {

    const val githubProfileUrl = "githubProfileUrl"

    //RecyclerView view type
    const val githubProfileRecyclerViewCell = 1

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

    //Internet connection variants

    //Connected
    const val wifi = 0
    const val cellular = 1

    //Disconnected
    const val disconnected = 2

    //Generic
    const val generic = 3
}