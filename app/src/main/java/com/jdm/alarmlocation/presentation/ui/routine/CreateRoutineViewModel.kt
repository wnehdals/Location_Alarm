package com.jdm.alarmlocation.presentation.ui.routine

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.domain.toLatLng
import com.jdm.alarmlocation.domain.toPlace
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoutineViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : BaseViewModel() {
    val isInData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val rangeData: SingleLiveEvent<Int> = SingleLiveEvent()
    val placeData: SingleLiveEvent<Place?> = SingleLiveEvent()
    val daysData: SingleLiveEvent<List<Int>> = SingleLiveEvent()

    var routineId: Long? = null
    var alarmIds: List<Long> = listOf()


    fun getRoutine(id: Long) {
        routineId = id
        viewModelScope.launch {
            /* CREATE */
            if (id == -1L) {
                placeData.value = null
                daysData.value = listOf<Int>()
                isInData.value = false
                rangeData.value = 50


            } else {
                /* UPDATE */
                val alarms = alarmRepository.getAlarmByRoutineId(id)
                alarmIds = alarms.map { it.id }
                if (alarms.isNotEmpty()) {
                    placeData.value = alarms[0].toPlace()
                    isInData.value = alarms[0].isIn
                    rangeData.value = alarms[0].range
                    daysData.value = alarms.map { it.day }
                } else {
                    return@launch
                }
            }

        }
    }

    fun saveAlarm() {
        viewModelScope.launch {
            val latLng = placeData.value!!.toLatLng()
            if (routineId!! == -1L) {
                val id = alarmRepository.insertRoutine()
                daysData.value!!.forEach {
                    val alarm = Alarm(
                        routineId = id,
                        placeTitle = placeData.value!!.title,
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        range = rangeData.value!!,
                        isIn = isInData.value!!,
                        day = it,
                        hour = 0,
                        minute = 0
                    )
                    alarmRepository.insert(alarm)
                }
            } else {
                alarmIds.forEach { alarmRepository.deleteAlarm(it) }
                daysData.value!!.forEach {
                    val alarm = Alarm(
                        routineId = routineId!!,
                        placeTitle = placeData.value!!.title,
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        range = rangeData.value!!,
                        isIn = isInData.value!!,
                        day = it,
                        hour = 0,
                        minute = 0
                    )
                    alarmRepository.insert(alarm)
                }
            }
        }
    }

    fun removeRoutine() {
        viewModelScope.launch {
            if (routineId != -1L) {
                alarmRepository.deleteRoutine(routineId!!)
                alarmIds.forEach {
                    alarmRepository.deleteAlarm(it)
                }
            }
        }
    }
}