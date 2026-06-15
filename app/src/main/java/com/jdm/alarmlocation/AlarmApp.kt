package com.jdm.alarmlocation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.jdm.alarmlocation.presentation.service.routine.RoutineGeofenceManager
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AlarmApp: Application() {

    @Inject
    lateinit var routineGeofenceManager: RoutineGeofenceManager

    override fun onCreate() {
        super.onCreate()
        val basicName = getString(R.string.str_foreground_basic_channel_name)
        val alarmName = getString(R.string.str_foreground_alarm_channel_name)
        createNotificationChannel(basicName, BASIC_CHANNEL_ID)
        createNotificationChannel(alarmName, ALARM_CHANNEL_ID)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_CLIEND_ID)
        // 신규 LocationRoutine 지오펜스 동기화 시작 (ON/OFF·생성·수정·삭제 자동 반영).
        routineGeofenceManager.start()
    }

    private fun createNotificationChannel(channelName: String, channelId: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val basicChannel = NotificationChannel(channelId, channelName, importance)
        basicChannel.description = channelName
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(basicChannel)
    }
    companion object {
        const val BASIC_CHANNEL_ID = "ForegroundServiceChannel"
        const val ALARM_CHANNEL_ID = "AlarmChannel"
    }
}