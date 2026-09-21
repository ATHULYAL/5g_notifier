package com.example.a5gnotifier

import android.content.Context
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager

class NetworkMonitor(
    private val context: Context,
    private val onNetworkChanged: (String) -> Unit
) {

    private val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val callback = object : TelephonyCallback(),
        TelephonyCallback.DisplayInfoListener {

        override fun onDisplayInfoChanged(displayInfo: TelephonyDisplayInfo) {

            val network = when (displayInfo.overrideNetworkType) {

                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA,
                TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED -> "5G"

                else -> "4G"
            }

            onNetworkChanged(network)
        }
    }

    fun start() {
        telephonyManager.registerTelephonyCallback(
            context.mainExecutor,
            callback
        )
    }

    fun stop() {
        telephonyManager.unregisterTelephonyCallback(callback)
    }
}