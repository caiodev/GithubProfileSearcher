package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.cast.ValueCasting.castValue
import githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.extensions.emitValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkChecking {

    private val mutableNetworkStateFlow = MutableStateFlow(false)
    private val networkStateFlow: StateFlow<Boolean>
        get() = mutableNetworkStateFlow

    private val networkRequest = NetworkRequest.Builder().apply {
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    }.build()

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            mutableNetworkStateFlow.emitValue(true)
        }

        override fun onLost(network: Network) {
            mutableNetworkStateFlow.emitValue(false)
        }
    }

    // Checks whether or not there is internet connection
    fun checkIfInternetConnectionIsAvailable(
        applicationContext: Context,
        onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) =
        handleInternetConnectionAvailability(
            castValue(applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)),
            onConnectionAvailable,
            onConnectionUnavailable
        )

    private fun handleInternetConnectionAvailability(
        connectivityManager: ConnectivityManager,
        onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) {
        if (connectivityManager.allNetworks.isNotEmpty()) {
            iterateOverTheListOfNetworks(connectivityManager, onConnectionAvailable)
        } else {
            onConnectionUnavailable()
        }
    }

    private fun iterateOverTheListOfNetworks(
        connectivityManager: ConnectivityManager,
        onConnectionAvailable: () -> Unit
    ) {
        connectivityManager.allNetworks.forEach { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    onConnectionAvailable()
                }
            }
        }
    }

    // Returns a StateFlow so internet connection related state changes can be observed
    fun observeInternetConnectionAvailability(applicationContext: Context): StateFlow<Boolean> {
        castValue<ConnectivityManager>(applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)).apply {
            requestNetwork(networkRequest, connectivityCallback)
        }
        return networkStateFlow
    }
}
