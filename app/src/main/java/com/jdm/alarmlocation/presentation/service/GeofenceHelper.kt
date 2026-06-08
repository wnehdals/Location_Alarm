package com.jdm.alarmlocation.presentation.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

data class GeofenceHelper(
    val context: Context
) {
    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(context)
    }

    fun geofencePendingIntent(id: Long): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.putExtra("id", id)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(id: Long, lat: Double, lng: Double, radius: Float, isIn: Boolean, listener: OnAddCallbackListener) {
        // 1. 이벤트 타입 결정 (들어오면, 벗어나면, 혹은 둘 다)
        val transitionTypes = if (isIn) Geofence.GEOFENCE_TRANSITION_ENTER else Geofence.GEOFENCE_TRANSITION_EXIT

        // 2. 지오펜스 객체 생성
        val geofence = Geofence.Builder()
            .setRequestId("routine_${id}") // 고유 ID
            .setCircularRegion(lat, lng, radius) // 위도, 경도, 반경(m)
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // 무제한 유지
            .setTransitionTypes(transitionTypes)
            .build()

        // 3. 요청 생성
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER) // 등록 시점에 이미 안에 있으면 즉시 트리거
            .addGeofence(geofence)
            .build()

        // 4. 시스템에 등록
        geofencingClient.addGeofences(request, geofencePendingIntent(id)).run {
            addOnSuccessListener { listener.onSuccess() }
            addOnFailureListener { listener.onFailure() }
        }
    }
    @SuppressLint("MissingPermission")
    fun removeGeofence(id: Long, listener: OnRemoveCallbackListener) {
        geofencingClient.removeGeofences(listOf("routine_${id}")).run {
            addOnSuccessListener { listener.onSuccess() }
            addOnFailureListener { listener.onFailure() }
        }
    }
    interface OnRemoveCallbackListener {
        fun onSuccess()
        fun onFailure()
    }
    interface OnAddCallbackListener {
        fun onSuccess()
        fun onFailure()
    }
}