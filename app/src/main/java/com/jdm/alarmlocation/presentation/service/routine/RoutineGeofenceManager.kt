package com.jdm.alarmlocation.presentation.service.routine

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 신규 [LocationRoutine] 기준 지오펜스 등록/해제를 일원화한다.
 *
 * [RoutineRepository.routines] 변화를 구독하여 ON + 오늘 요일에 해당하는 루틴만 지오펜스를 유지한다
 * (토글 ON/OFF·생성·수정·삭제 시 자동 반영). 자정 요일 경계는 [syncNow] 로 [com.jdm.alarmlocation.presentation.service.MidnightReceiver]
 * 가 갱신한다. 레거시 `routine_<id>` 와 충돌하지 않도록 `lr_<id>` requestId 를 사용한다.
 */
@Singleton
class RoutineGeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val routineRepository: RoutineRepository,
) {
    private val client: GeofencingClient by lazy { LocationServices.getGeofencingClient(context) }
    private val scope = CoroutineScope(SupervisorJob())
    private val registered = mutableSetOf<Long>()
    private var started = false

    /** 앱 시작 시 1회 호출. 루틴 변화를 지속 구독해 지오펜스를 동기화한다. */
    fun start() {
        if (started) return
        started = true
        scope.launch {
            routineRepository.routines.collect { reconcile(it) }
        }
    }

    /** 요일 경계(자정) 등에서 강제 재동기화. */
    suspend fun syncNow() {
        reconcile(routineRepository.routines.first())
    }

    private fun reconcile(routines: List<LocationRoutine>) {
        val desired = routines.filter { it.isOn && ScheduleUtil.isScheduledToday(it) }
        val desiredIds = desired.map { it.id }.toSet()

        (registered - desiredIds).forEach { removeGeofence(it) }
        desired.forEach { addGeofence(it) }

        registered.clear()
        registered.addAll(desiredIds)
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(routine: LocationRoutine) {
        if (!hasLocationPermission()) return
        val transition = if (routine.direction == AlarmDirection.ENTER) {
            Geofence.GEOFENCE_TRANSITION_ENTER
        } else {
            Geofence.GEOFENCE_TRANSITION_EXIT
        }
        val geofence = Geofence.Builder()
            .setRequestId(requestId(routine.id))
            .setCircularRegion(routine.latitude, routine.longitude, routine.radiusMeters.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transition)
            .build()
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        client.addGeofences(request, pendingIntent()).run {
            addOnFailureListener { Log.w(TAG, "addGeofence failed for ${routine.id}: ${it.message}") }
        }
    }

    private fun removeGeofence(id: Long) {
        client.removeGeofences(listOf(requestId(id)))
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, RoutineGeofenceReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
    }

    companion object {
        private const val TAG = "RoutineGeofence"
        private const val PREFIX = "lr_"
        fun requestId(id: Long) = "$PREFIX$id"
        fun parseId(requestId: String): Long? = requestId.removePrefix(PREFIX).toLongOrNull()
    }
}
