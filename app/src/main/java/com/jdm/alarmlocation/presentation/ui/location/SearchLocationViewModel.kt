package com.jdm.alarmlocation.presentation.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.Location
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.domain.repository.LocationRepository
import com.jdm.alarmlocation.presentation.service.LocationManager
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository
) : BaseViewModel() {
    private var lm: LocationManager? = null
    private var location: Location? = null
    private var locationReceiveCnt = 0

    var selectedNameLocation: NameLocation? = null
    val searchResultMsgData = SingleLiveEvent<String>()
    val direction = SingleLiveEvent<String>()
    val locationList = SingleLiveEvent<List<NameLocation>>()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationReceiveCnt >= 1) {
                releaseLocation()
                getAddress()
            }
            locationReceiveCnt++
            for (loc in locationResult.locations) {
                if (loc != null) {
                    val latitude = loc.latitude
                    val longitude = loc.longitude
                    location = Location(latitude, longitude)

                }
            }
        }
    }

    fun getLocationInfo(context: Context) {
        releaseLocation()
        lm = LocationManager(context, locationCallback)
        locationReceiveCnt = 0
        lm?.startLocationUpdates()
    }

    fun releaseLocation() {
        lm?.stopLocationUpdates()
    }

    fun getAddress() {
        try {
            if (location == null) {
                searchResultMsgData.value = "현재 위치를 찾을 수 없습니다."
                return
            }
            val geocoder =
                Geocoder(context, Locale.KOREA).getFromLocation(location!!.lat, location!!.long, 1)
            if (geocoder == null) {
                searchResultMsgData.value = "현재 위치를 찾을 수 없습니다."
                return
            }
            val address = geocoder[0].getAddressLine(0)


                insertLocation(
                    name = address,
                    latitude = location!!.lat,
                    longitude = location!!.long
                )

            selectedNameLocation = NameLocation(name = address, latitude = location!!.lat, longitude = location!!.long)
            direction.value = "finish"



        } catch (e: Exception) {
            searchResultMsgData.value = "현재 위치를 찾을 수 없습니다."
            e.printStackTrace()

        }
    }

    fun insertLocation(name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            locationRepository.insertLocation(
                name = name,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    fun getLocationList() {
        viewModelScope.launch {
            locationRepository.getAllLocation().collect() {
                locationList.value = it
            }
        }
    }

}
