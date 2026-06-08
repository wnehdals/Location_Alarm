package com.jdm.alarmlocation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlarmApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val basicName = getString(R.string.str_foreground_basic_channel_name)
        val alarmName = getString(R.string.str_foreground_alarm_channel_name)
        createNotificationChannel(basicName, BASIC_CHANNEL_ID)
        createNotificationChannel(alarmName, ALARM_CHANNEL_ID)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_CLIEND_ID)
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