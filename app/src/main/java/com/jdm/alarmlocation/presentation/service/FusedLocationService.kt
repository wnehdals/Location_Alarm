package com.jdm.alarmlocation.presentation.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.presentation.ui.main.MainActivity
import com.jdm.alarmlocation.presentation.util.Const.ACTION_START_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.ACTION_STOP_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.LOCATION_SERVICE_ID
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FusedLocationService : Service() {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    val range = 0.0002
    private var alarm: Alarm? = null
    val alarmList = mutableListOf<Alarm>()
    /*
    val maxLatitude = VOGO_LATITUDE + alpha
    val minLatitude = VOGO_LATITUDE - alpha
    val maxLongitude = VOGO_LONGITUDE + alpha
    val minLongitude = VOGO_LONGITUDE - alpha

     */
    lateinit var notificationManager: NotificationManagerCompat
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private val notiPermission = android.Manifest.permission.POST_NOTIFICATIONS
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            for (location in locationResult.locations) {
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    Log.e("service", "${latitude} / ${longitude}")
                    if (alarm == null)
                        return
                    if (isValide(latitude, longitude)) {
                        var way = if (alarm!!.way == 0) "진입하였습니다." else "벗어났습니다."
                        var title = "위치 알람"
                        var body = "${alarm!!.address}를 ${way}"
                        var channelId = "위치 알람"
                        var pushNotiId = 1
                        val intent = Intent(this@FusedLocationService, MainActivity::class.java)
                        intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                        )
                        var notification = getNotification(title, body, channelId, pushNotiId,intent)
                        if (this@FusedLocationService.checkSelfPermission(notiPermission) == PackageManager.PERMISSION_GRANTED) {
                            notificationManager.notify(pushNotiId, notification)
                        }
                    }
                }
            }
        }
    }
    private fun isValidIn(latitude: Double, longitude: Double): Boolean {
        if (alarm == null)
             return false
        val distance = distance(latitude, longitude, alarm!!.latitude, alarm!!.longitude)
        Log.e("isValidIn", "${distance} <= ${alarm!!.range}")
        if (distance <= alarm!!.range) {
            return true
        } else {
            return false
        }
    }
    private fun isValideOut(latitude: Double, longitude: Double) : Boolean {
        if (alarm == null)
            return false
        val distance = distance(latitude, longitude, alarm!!.latitude, alarm!!.longitude)
        Log.e("isValideOut", "${distance} >= ${alarm!!.range}")
        if (distance >= alarm!!.range) {
            return true
        } else {
            return false
        }
    }
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515 * 1609.344
        return dist //단위 meter
    }
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    //radian(라디안)을 10진수로 변환
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }
    private fun isValideTime() : Boolean{
        if (alarm == null)
            return false

        val hour  = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)
        val leftTImeTotal = alarm!!.leftTimeHour * 60 + alarm!!.leftTImeMinute
        val rightTimeTotal = alarm!!.rightTimeHour * 60 + alarm!!.rightTimeMinute
        val currentTimeTotal = hour * 60 + minute
        Log.e("isValideTime", "${leftTImeTotal} <= ${currentTimeTotal} <= ${rightTimeTotal}" )
        if (leftTImeTotal <= currentTimeTotal && currentTimeTotal <= rightTimeTotal) {
            return true
        } else {
            return false
        }
    }

    private fun isValide(latitude: Double, longitude: Double): Boolean {
        if (alarm == null)
            return false
        else {
            if (isValideTime()) {
                if (alarm!!.way == 0) {
                    if (isValidIn(latitude, longitude)) {
                        return true
                    } else {
                        return false
                    }
                } else {
                    if (isValideOut(latitude, longitude)) {
                        return true
                    } else {
                        return false
                    }
                }
            } else {
                return false
            }
        }

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = NotificationManagerCompat.from(applicationContext)
        if (intent != null) {
            val action = intent.action
            alarm = intent.getParcelableExtra("alarm")?: null
            if (alarm == null) {
                stopLocationUpdates()
                return START_NOT_STICKY
            }
            if (action != null) {
                if (action == ACTION_START_LOCATION_SERVICE) {
                    startLocationUpdates(alarm)
                } else if (action == ACTION_STOP_LOCATION_SERVICE) {
                    stopLocationUpdates()
                }
            }
        } else {
            return Service.START_REDELIVER_INTENT //서비스가 비정상 종료되었다고 판단하고 재시작
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    fun getNotification(title: String, body: String, channelId: String, pushNotiId: Int, intent: Intent, setOnGoing: Boolean = false): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder: NotificationCompat.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                var channel = NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(applicationContext, channelId)
        } else {
            builder = NotificationCompat.Builder(applicationContext)
        }
        builder
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(
                R.drawable.ic_logo_noti
            )
            .setColor(ContextCompat.getColor(applicationContext, R.color.green_200))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(setOnGoing)

        return builder.build()
    }
    fun startLocationUpdates(alarm: Alarm?) {
        if (alarm == null)
            return
        var title = "위치 알람"
        var body = "위치 알람을 중지하려면 탭하여 알람을 OFF시켜주세요."
        var channelId = "위치 알람"
        val intent = Intent(this, MainActivity::class.java)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("PERMISSION", "PERMISSION")
            startActivity(intent)
            return
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
        startForeground(LOCATION_SERVICE_ID, getNotification(title = title, body = body, channelId = channelId, pushNotiId = 0, intent = intent, setOnGoing = true))
    }
    fun hasPermission(): Boolean {
        return permissions.filter { this.checkSelfPermission(it) == PackageManager.PERMISSION_DENIED }.isEmpty()
    }
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }
    fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(10)
            fastestInterval = TimeUnit.SECONDS.toMillis(5)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}