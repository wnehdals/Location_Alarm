package com.jdm.alarmlocation.presentation.ui.add

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.jdm.alarmlocation.presentation.service.LocationManager
import com.jdm.alarmlocation.presentation.ui.add.LocationSetContract.LocationSetEvent
import com.jdm.alarmlocation.presentation.ui.add.LocationSetContract.LocationSetSideEffect
import com.jdm.alarmlocation.presentation.ui.add.LocationSetContract.LocationSetState
import com.jdm.core.base.BaseViewModel
import com.jdm.data.repository.LocationRepository
import com.jdm.model.Location
import com.jdm.model.NameLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationSetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository
) : BaseViewModel<LocationSetState, LocationSetSideEffect, LocationSetEvent>(
    LocationSetState()
) {
    private var lm: LocationManager? = null
    private var location: Location? = null
    private var locationReceiveCnt = 0
    var selectedNameLocation: NameLocation? = null
    override fun handleEvents(event: LocationSetEvent) {
        when (event) {
            is LocationSetEvent.SetDialogVisibility -> {
                updateState {
                    copy(isShowPermissionDialog = event.isShow)
                }
            }

            is LocationSetEvent.SearchLocationEvent -> {
                sendEffect({
                    LocationSetSideEffect.RequestPermission
                })
            }
            is LocationSetEvent.OnClickRecentlyLocation -> {
                selectedNameLocation = event.nameLocation
                sendEffect({
                    LocationSetSideEffect.SendActivityResult
                })
            }
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            Log.e("location", "receive : ${locationReceiveCnt}")
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

                    Log.e("location", "latitude : ${latitude} / longitude : ${longitude}")
                }
            }
        }
    }

    fun getLocationInfo(context: Context) {
        if (currentState.locationSearchState is LocationSearchState.Undefine) {
            updateState { copy(locationSearchState = LocationSearchState.Loading()) }
            lm = LocationManager(context, locationCallback)
            locationReceiveCnt = 0
            lm?.startLocationUpdates()
        }
    }

    fun releaseLocation() {
        lm?.stopLocationUpdates()
    }

    fun getAddress() {
        try {
            if (location == null) {
                updateState { copy(locationSearchState = LocationSearchState.Fail) }
                return
            }
            val geocoder =
                Geocoder(context, Locale.KOREA).getFromLocation(location!!.lat, location!!.long, 1)
            if (geocoder == null) {
                updateState { copy(locationSearchState = LocationSearchState.Fail) }
                return
            }
            val address = geocoder.first().getAddressLine(0)
            val nameList = currentState.locationList.asSequence().map { it.name }
            if (!nameList.contains(address)) {
                insertLocation(
                    name = address,
                    latitude = location!!.lat,
                    longitude = location!!.long
                )
            }
            selectedNameLocation = NameLocation(name = address, latitude = location!!.lat, longitude = location!!.long)
            updateState {
                copy(
                    locationSearchState = LocationSearchState.Success(
                        address = address
                    )
                )
            }
            sendEffect({ LocationSetContract.LocationSetSideEffect.SendActivityResult })


        } catch (e: Exception) {
            updateState {
                copy(
                    locationSearchState = LocationSearchState.Fail
                )
            }
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
                updateState {
                    copy(locationList = it)
                }
            }
        }
    }

}
