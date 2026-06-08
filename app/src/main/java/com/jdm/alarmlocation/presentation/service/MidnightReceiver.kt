package com.jdm.alarmlocation.presentation.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.Calendar
import javax.inject.Inject
@AndroidEntryPoint
class MidnightReceiver : BroadcastReceiver() {
    @Inject lateinit var repository: AlarmRepository
    private val coroutineScope = CoroutineScope(Job())
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_DAILY_MIDNIGHT_UPDATE" || intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("MidnightReceiver", "자정 업데이트 시작: 오늘 요일에 맞는 지오펜스 재설정")

            // 1. 알람 리스트를 가져옵니다.
            coroutineScope.launch(Dispatchers.IO) {
                val onRoutines = repository.getAllRoutine()
                    .filter { it.isOn }
                    .map { it.id }

                val alarms = repository.getAllAlarm()
                alarms.forEach {
                    if (onRoutines.contains(it.routineId)) {
                        refreshGeofenceBySchedule(it, context)
                    } else {
                        removeGeofenceBySchedule(it, context)
                    }
                }
                scheduleMidnightUpdate(context)
            }
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleMidnightUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MidnightReceiver::class.java).apply {
            action = "ACTION_DAILY_MIDNIGHT_UPDATE"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9999, // 자정 업데이트용 고유 ID
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 내일 자정 시간 계산
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Doze 모드에서도 깨어나도록 setExactAndAllowWhileIdle 사용
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun removeGeofenceBySchedule(alarm: Alarm, context: Context) {
        val geofenceHelper = GeofenceHelper(context)
        geofenceHelper.removeGeofence(
            alarm.id,
            object : GeofenceHelper.OnRemoveCallbackListener {
                override fun onSuccess() {
                }

                override fun onFailure() {
                }
            }
        )
    }


    fun refreshGeofenceBySchedule(alarm: Alarm, context: Context) {
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_WEEK)
        val geofenceHelper = GeofenceHelper(context)

        if (alarm.day == today) {
            // 오늘이 알람 날짜라면 -> 지오펜스 등록 (위치 탐색 시작)
            geofenceHelper.addGeofence(
                alarm.id,
                alarm.latitude,
                alarm.longitude,
                alarm.range.toFloat(),
                alarm.isIn,
                listener = object : GeofenceHelper.OnAddCallbackListener {
                    override fun onSuccess() {
                        Log.e("alarm", "${alarm}")
                    }

                    override fun onFailure() {
                    }
                }
            )
        } else {
            // 오늘이 아니면 -> 지오펜스 제거 (배터리 절약)
            geofenceHelper.removeGeofence(
                alarm.id,
                object : GeofenceHelper.OnRemoveCallbackListener {
                    override fun onSuccess() {
                    }

                    override fun onFailure() {
                    }
                }
            )
        }
    }
}