@file:Suppress("DEPRECATION")

package com.roopasn.tawkto.domain.usecases.user_list

import android.content.Context
import com.roopasn.tawkto.infrastructure.network.ConnectivityObserver
import com.roopasn.tawkto.infrastructure.network.NetworkConnectivityObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Network change notification use case. Only when changed it will be notified
 *
 * @param mContext context
 */
@Suppress("KotlinConstantConditions")
class NetworkConnectivityUseCase @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mNetworkObserver: NetworkConnectivityObserver
) {
    /**
     * observer which will be notified on network change with current connectivity boolean status
     */
    var observerListener: (ConnectivityObserver.Status) -> Unit = {}

//    private val mConnectivityManager =
//        mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

//    private var mListener: ConnectivityManager.NetworkCallback? = null
//
//    private var mNetworkReceiver: NetworkReceiver? = null
//
//    private var mIsConnected = true

    /**
     * Observer for network change, this trigger this use case to execute
     */
    fun observer() {
        CoroutineScope(Dispatchers.Default).launch {
            mNetworkObserver.observe().collectLatest { status ->
                observerListener(status)
            }
        }
    }


    /*fun observer1() {
        if (Util.getSdkInt() >= Build.VERSION_CODES.N) {
            if (null == mListener) {
                val listener = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        if (!mIsConnected) {
                            mIsConnected = true
                            observer(mIsConnected)
                        }
                    }

                    override fun onLost(network: Network) {
                        if (mIsConnected) {
                            mIsConnected = false
                            observer(mIsConnected)
                        }
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        capabilities: NetworkCapabilities
                    ) {
                        val isConnected =
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                        if (isConnected != mIsConnected) {
                            mIsConnected = isConnected
                            observer(mIsConnected)
                        }
                    }
                }

                mConnectivityManager.registerDefaultNetworkCallback(listener)
                mListener = listener
            }
        } else {
            // For older versions of Android
            val networkInfo = mConnectivityManager.activeNetworkInfo
            mIsConnected = networkInfo?.isConnected == true
            observer(mIsConnected)

            mNetworkReceiver = NetworkReceiver(mContext, observer, mIsConnected)
        }
    }*/

    /**
     * Clean up and unregister calls
     */
    fun cleanUp() {
        /*if (Util.getSdkInt() >= Build.VERSION_CODES.N) {
            mListener?.let { mConnectivityManager.unregisterNetworkCallback(it) }
            mListener = null
        } else {
            mNetworkReceiver?.let { mContext.unregisterReceiver(it.receiver) }
            mNetworkReceiver = null
        }*/
    }
}

/*
private class NetworkReceiver(context: Context, observer: (Boolean) -> Unit, isConnected: Boolean) {
    private var mIsConnectedInReceiver = isConnected

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected1 = networkInfo?.isConnected == true

            if (isConnected1 != mIsConnectedInReceiver) {
                mIsConnectedInReceiver = isConnected1
                observer(mIsConnectedInReceiver)
            }
        }
    }

    init {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, filter)
    }
}*/
