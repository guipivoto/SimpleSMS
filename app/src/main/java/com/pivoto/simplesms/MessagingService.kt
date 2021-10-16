package com.pivoto.simplesms

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Created by pivoto on 17/10/2017.
 */
class MessagingService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        stopSelf()
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val TAG = "SimpleSMS"
    }
}