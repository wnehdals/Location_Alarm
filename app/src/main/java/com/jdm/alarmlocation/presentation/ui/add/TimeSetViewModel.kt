package com.jdm.alarmlocation.presentation.ui.add

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.jdm.alarmlocation.presentation.service.LocationManager
import com.jdm.core.base.BaseViewModel
import com.jdm.alarmlocation.presentation.ui.add.LocationSetContract.LocationSetEvent
import com.jdm.alarmlocation.presentation.ui.add.LocationSetContract.LocationSetState
import com.jdm.alarmlocation.presentation.ui.add.LocationSetContract.LocationSetSideEffect
import com.jdm.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TimeSetViewModel @Inject constructor(
) : BaseViewModel<TimeSetContract.TimeSetState, TimeSetContract.TimeSetSideEffect, TimeSetContract.TimeSetEvent>(
    TimeSetContract.TimeSetState.Loading()
) {

    override fun handleEvents(event: TimeSetContract.TimeSetEvent) {
    }

}
