package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ConnectionAvailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.ConnectionUnavailable
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.Generic
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.base.states.State
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.emitValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkChecking(private val connectivityManager: ConnectivityManager) {

    private val _networkStateFlow = MutableStateFlow<State<*>>(ConnectionAvailable)
    private val networkStateFlow: StateFlow<State<*>>
        get() = _networkStateFlow

    private val networkRequest = NetworkRequest.Builder().apply {
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    }.build()

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            _networkStateFlow.emitValue(ConnectionAvailable)
        }

        override fun onLost(network: Network) {
            _networkStateFlow.emitValue(ConnectionUnavailable)
        }
    }

    fun checkIfInternetConnectionIsAvailable() = handleInternetConnectionAvailability()

    private fun handleInternetConnectionAvailability(): State<*> {
        return if (connectivityManager.allNetworks.isNotEmpty()) {
            iterateOverTheListOfNetworks()
        } else {
            ConnectionUnavailable
        }
    }

    private fun iterateOverTheListOfNetworks(): State<*> {
        connectivityManager.allNetworks.forEach { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    return ConnectionAvailable
                }
            }
        }
        return Generic
    }

    fun observeInternetConnectionAvailability(): StateFlow<State<*>> {
        connectivityManager.requestNetwork(networkRequest, connectivityCallback)
        return networkStateFlow
    }
}
