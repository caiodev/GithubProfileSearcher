package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.*
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.emitValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkChecking(private val manager: ConnectivityManager) {

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
            _networkStateFlow.emitValue(Available)
        }

        override fun onLost(network: Network) {
            _networkStateFlow.emitValue(Unavailable)
        }
    }

    fun obtainConnectionObserver(): StateFlow<State<Connection>> {
        manager.registerNetworkCallback(networkRequest, connectivityCallback)
        return networkStateFlow
    }

    fun checkIfConnectionIsAvailable(): State<Connection> {
        manager.run {
            getNetworkCapabilities(activeNetwork)?.run {
                val isAnyTransportMethodAvailable =
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                return if (isAnyTransportMethodAvailable) {
                    Available
                } else {
                    Unavailable
                }
            } ?: run {
                return Unavailable
            }
        }
    }
}
