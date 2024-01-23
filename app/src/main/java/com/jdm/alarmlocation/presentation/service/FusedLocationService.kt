package com.jdm.alarmlocation.presentation.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FusedLocationService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}