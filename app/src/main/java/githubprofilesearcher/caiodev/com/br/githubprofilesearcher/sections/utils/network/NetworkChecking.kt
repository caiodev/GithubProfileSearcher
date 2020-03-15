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

    private val networkRequest = NetworkRequest.Builder().apply {
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            networkState.postValue(true)
        }

        override fun onLost(network: Network) {
            networkState.postValue(false)
        }
    }

    //Checks whether or not there is internet connection
    fun checkIfInternetConnectionIsAvailable(
        applicationContext: Context, onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) =
        handleInternetConnectionAvailability(
            (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager),
            onConnectionAvailable,
            onConnectionUnavailable
        )

    //Returns a LiveData so internet connection related state changes can be observed
    fun internetConnectionAvailabilityObservable(applicationContext: Context): LiveData<Boolean> {
        (applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            requestNetwork(networkRequest.build(), connectivityCallback)
        }
        return networkState
    }

    private inline fun handleInternetConnectionAvailability(
        connectivityManager: ConnectivityManager,
        onConnectionAvailable: () -> Unit,
        onConnectionUnavailable: () -> Unit
    ) {
        if (connectivityManager.allNetworks.isNotEmpty()) {
            connectivityManager.allNetworks.forEach { network ->
                connectivityManager.getNetworkCapabilities(network)?.let { networkCapabilities ->
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) onConnectionAvailable.invoke()
                }
            }
        } else
            onConnectionUnavailable.invoke()
    }
}