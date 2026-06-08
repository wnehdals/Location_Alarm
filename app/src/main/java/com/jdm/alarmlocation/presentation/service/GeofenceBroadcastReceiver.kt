package com.jdm.alarmlocation.presentation.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.jdm.alarmlocation.AlarmApp.Companion.ALARM_CHANNEL_ID
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            Log.e("Geofence", "Error code: ${geofencingEvent.errorCode}")
            return
        }

        // 발생한 이벤트 타입 (진입 또는 이탈)
        val transitionType = geofencingEvent.geofenceTransition

        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.e("Geofence", "Error code: 진입")
                sendNotification(context, "목적지에 진입했습니다!", "설정한 반경 내로 들어왔습니다.")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                sendNotification(context, "목적지를 벗어났습니다!", "설정한 반경에서 나갔습니다.")
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun sendNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }
}