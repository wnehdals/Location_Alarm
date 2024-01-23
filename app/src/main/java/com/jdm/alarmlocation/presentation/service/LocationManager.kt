package com.jdm.alarmlocation.presentation.service

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.annotation.MainThread
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationManager constructor(
    private val context: Context,
    private val callback: LocationCallback
) {
    var isRunning = false
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(10)
        fastestInterval = TimeUnit.SECONDS.toMillis(5)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    /*
    private val locationCallback = object : LocationCallback() {
        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)

        }

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (location in locationResult.locations){
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    callback(latitude,longitude)
                    //Log.e("location","latitude : ${latitude} / longitude : ${longitude}")
                }
            }
        }
    }

     */

    @Throws(SecurityException::class)
    @MainThread
    fun startLocationUpdates() {
        if (isRunning) {
            return
        }
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, context.mainLooper)
        } catch (permissionRevoked: SecurityException) {

            Log.d("locationmanager", "Location permission revoked; details: $permissionRevoked")
            throw permissionRevoked
        }
    }

    @MainThread
    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
        isRunning = false
    }
    fun isPermissionAllow() : Boolean{
        val permission = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        return permission.none { context.checkSelfPermission(it) != PackageManager.PERMISSION_DENIED }
    }
}