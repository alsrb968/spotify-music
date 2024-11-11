package com.litbig.spotify.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UsbReceiver(
    private val onUsbMounted: (String) -> Unit,
    private val onUsbEjected: (String) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_MEDIA_MOUNTED -> {
                val path = intent.data?.path ?: return
                onUsbMounted(path)
            }

            Intent.ACTION_MEDIA_EJECT -> {
                val path = intent.data?.path ?: return
                onUsbEjected(path)
            }
        }
    }
}