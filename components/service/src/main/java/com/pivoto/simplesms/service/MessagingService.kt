package com.pivoto.simplesms.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MessagingService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        stopSelf()
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}