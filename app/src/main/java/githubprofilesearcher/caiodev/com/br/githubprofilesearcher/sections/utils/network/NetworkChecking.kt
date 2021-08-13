package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.emitValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkChecking(private val connectivityManager: ConnectivityManager) {

    private val _networkStateFlow = MutableStateFlow<State<Connection>>(
        InitialConnection
    )
    private val networkStateFlow: StateFlow<State<Connection>>
        get() = _networkStateFlow

    private val networkRequest = NetworkRequest.Builder().apply {
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    }.build()

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            println("NETWORKPROBE: PostAvailable")
            _networkStateFlow.emitValue(Available)
        }

        override fun onLost(network: Network) {
            println("NETWORKPROBE: PostUnavailable")
            _networkStateFlow.emitValue(Unavailable)
        }
    }

    fun checkIfConnectionIsAvailable() = handleConnection()

    private fun handleConnection(): State<Connection> {
        return if (connectivityManager.allNetworks.isNotEmpty()) {
            iterateOverTheListOfNetworks()
        } else {
            println("NETWORKPROBE: ReturnUnavailable")
            Unavailable
        }
    }

    private fun iterateOverTheListOfNetworks(): State<Connection> {
        connectivityManager.allNetworks.forEach { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    println("NETWORKPROBE: ReturnAvailable")
                    return Available
                }
            }
        }
        println("NETWORKPROBE: ReturnNeutral")
        return Neutral
    }

    fun observeConnection(): StateFlow<State<Connection>> {
        connectivityManager.requestNetwork(networkRequest, connectivityCallback)
        return networkStateFlow
    }
}
