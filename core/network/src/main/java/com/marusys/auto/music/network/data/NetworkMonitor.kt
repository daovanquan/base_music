package com.marusys.auto.music.network.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.marusys.auto.music.network.model.NetworkStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkMonitor constructor(
    private val context: Context
) {

    private val _state = MutableStateFlow(NetworkStatus.NOT_CONNECTED)

    val state: StateFlow<NetworkStatus>
        get() = _state

    init {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                _state.value = NetworkStatus.CONNECTED
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                _state.value = NetworkStatus.NOT_CONNECTED
            }
        }

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }


}