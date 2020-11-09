package githubprofilesearcher.caiodev.com.br.githubprofilesearcher.sections.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkChecking {

    private val networkState = MutableLiveData<Boolean>()
    private val networkStateLiveData: LiveData<Boolean>
        get() = networkState

    private val networkRequest = NetworkRequest.Builder().apply {
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    }.build()

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            networkState.postValue(true)
        }

        override fun onLost(network: Network) {
            networkState.postValue(false)
        }
    }

    // Checks whether or not there is internet connection
    suspend fun checkIfInternetConnectionIsAvailable(
        applicationContext: Context,
        onConnectionAvailable: suspend () -> Unit,
        onConnectionUnavailable: suspend () -> Unit
    ) =
        handleInternetConnectionAvailability(
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
            onConnectionAvailable,
            onConnectionUnavailable
        )

    // Returns a LiveData so internet connection related state changes can be observed
    fun internetConnectionAvailabilityObservable(applicationContext: Context): LiveData<Boolean> {
        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            requestNetwork(networkRequest, connectivityCallback)
        }
        return networkStateLiveData
    }

    private suspend fun handleInternetConnectionAvailability(
        connectivityManager: ConnectivityManager,
        onConnectionAvailable: suspend () -> Unit,
        onConnectionUnavailable: suspend () -> Unit
    ) {
        if (connectivityManager.allNetworks.isNotEmpty()) {
            iterateOverTheListOfNetworks(connectivityManager, onConnectionAvailable)
        } else {
            onConnectionUnavailable.invoke()
        }
    }

    private suspend fun iterateOverTheListOfNetworks(
        connectivityManager: ConnectivityManager,
        onConnectionAvailable: suspend () -> Unit
    ) {
        connectivityManager.allNetworks.forEach { network ->
            connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    onConnectionAvailable.invoke()
                }
            }
        }
    }
}
