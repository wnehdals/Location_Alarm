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
import android.location.Location
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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

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
            var loc = locationResult.locations.firstOrNull()
            if (loc == null) return
            val latitude = loc.latitude
            val longitude = loc.longitude
            Log.e("service", "${latitude} / ${longitude}")
            if (alarm == null) return

            if (isValide(latitude, longitude)) {
                var way = if (alarm!!.isIn) "진입하였습니다." else "벗어났습니다."
                var title = "위치 알람"
                var body = "${alarm!!.placeTitle}를 ${way}"
                var channelId = "위치 알람"
                var pushNotiId = 1
                val intent = Intent(this@FusedLocationService, MainActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
                var notification =
                    getNotification(title, body, channelId, pushNotiId, intent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (this@FusedLocationService.checkSelfPermission(notiPermission) == PackageManager.PERMISSION_GRANTED) {
                        notificationManager.notify(pushNotiId, notification)
                    }
                } else {
                    notificationManager.notify(pushNotiId, notification)
                }
            }
        }
    }

    private fun isValidIn(latitude: Double, longitude: Double): Boolean {
        if (alarm == null)
            return false
        val distance = distanceMetersAndroid(latitude, longitude, alarm!!.latitude, alarm!!.longitude
        )
        Log.e("isValidIn", "${distance} <= ${alarm!!.range}")
        if (distance <= alarm!!.range) {
            return true
        } else {
            return false
        }
    }

    private fun isValideOut(latitude: Double, longitude: Double): Boolean {
        if (alarm == null)
            return false
        val distance = distanceMetersAndroid(
                latitude,
                longitude
            ,alarm!!.latitude, alarm!!.longitude
        )
        Log.e("isValideOut", "${distance} >= ${alarm!!.range}")
        if (distance >= alarm!!.range) {
            return true
        } else {
            return false
        }
    }

    private fun isValide(latitude: Double, longitude: Double): Boolean {
        if (alarm == null)
            return false
        else {
            if (alarm!!.isIn) {
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
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = NotificationManagerCompat.from(applicationContext)
        if (intent != null) {
            val action = intent.action
            alarm = intent.getParcelableExtra("alarm") ?: null
            if (alarm == null) {
                stopLocationUpdates()
            }
            if (action != null) {
                if (action == ACTION_START_LOCATION_SERVICE) {
                    startLocationUpdates(alarm)
                } else if (action == ACTION_STOP_LOCATION_SERVICE) {
                    stopLocationUpdates()
                }
            }
        } else {
        }
        //return super.onStartCommand(intent, flags, startId)
        return Service.START_REDELIVER_INTENT //서비스가 비정상 종료되었다고 판단하고 재시작
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    fun getNotification(
        title: String,
        body: String,
        channelId: String,
        pushNotiId: Int,
        intent: Intent,
        setOnGoing: Boolean = false
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder: NotificationCompat.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                var channel =
                    NotificationChannel(channelId, "알림", NotificationManager.IMPORTANCE_HIGH)
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
            .setAutoCancel(false)

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
        fusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
        startForeground(
            LOCATION_SERVICE_ID,
            getNotification(
                title = title,
                body = body,
                channelId = channelId,
                pushNotiId = 0,
                intent = intent,
                setOnGoing = true
            ),
        )
    }

    fun hasPermission(): Boolean {
        return permissions.filter { this.checkSelfPermission(it) == PackageManager.PERMISSION_DENIED }
            .isEmpty()
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

    fun distanceMetersAndroid(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        // results[0]는 항상 >= 0 (미터)
        return if (results.isNotEmpty() && !results[0].isNaN()) {
            kotlin.math.abs(results[0]) // 안전을 위해 절댓값(보수적)
        } else {
            0f
        }
    }
}