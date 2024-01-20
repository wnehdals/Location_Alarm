package com.jdm.alarmlocation.presentation.ui.add

import androidx.lifecycle.viewModelScope
import com.jdm.core.base.BaseViewModel
import com.jdm.data.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddAlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : BaseViewModel<AddAlarmContract.AddAlarmState, AddAlarmContract.AddAlarmSideEffect, AddAlarmContract.AddAlarmEvent>(
    AddAlarmContract.AddAlarmState()
) {
    override fun handleEvents(event: AddAlarmContract.AddAlarmEvent) {
        when (event) {
            is AddAlarmContract.AddAlarmEvent.OnClickSearchLocation -> {
                sendEffect({ AddAlarmContract.AddAlarmSideEffect.GoToLocationSearch })
            }

            is AddAlarmContract.AddAlarmEvent.SendNameLocation -> {
                updateState { copy(nameLocation = event.nameLocation) }
            }

            is AddAlarmContract.AddAlarmEvent.OnClickLeftTimePickerConfirm -> {
                updateState {
                    val hour = event.calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = event.calendar.get(Calendar.MINUTE)
                    copy(
                        leftCalendar = event.calendar,
                        leftTimeText = "${String.format("%02d", hour)}:${
                            String.format(
                                "%02d",
                                minute
                            )
                        }"
                    )
                }
                compareCalendar(currentState.leftCalendar, currentState.rightCalendar)
            }

            is AddAlarmContract.AddAlarmEvent.OnClickRightTimePickerConfirm -> {
                updateState {
                    val hour = event.calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = event.calendar.get(Calendar.MINUTE)
                    copy(
                        rightCalendar = event.calendar,
                        rightTimeText = "${String.format("%02d", hour)}:${
                            String.format(
                                "%02d",
                                minute
                            )
                        }"
                    )
                }
                compareCalendar(currentState.leftCalendar, currentState.rightCalendar)
            }

            is AddAlarmContract.AddAlarmEvent.OnClickRangeSelect -> {
                updateState {
                    copy(range = event.range)
                }
            }

            is AddAlarmContract.AddAlarmEvent.OnClickWay -> {
                updateState {
                    copy(way = event.way)
                }
            }

            is AddAlarmContract.AddAlarmEvent.OnClickAddAlarmLocation -> {
                if (currentState.leftCalendar == null) {
                    sendEffect({
                        AddAlarmContract.AddAlarmSideEffect.ShowToast("알람이 울릴 시간을 설정해주세요.")
                    })
                    return
                }
                if (currentState.rightCalendar == null) {
                    sendEffect({
                        AddAlarmContract.AddAlarmSideEffect.ShowToast("알람이 울릴 시간을 설정해주세요.")
                    })
                    return
                }
                if (currentState.nameLocation.latitude == 0.0 || currentState.nameLocation.longitude == 0.0) {
                    sendEffect({
                        AddAlarmContract.AddAlarmSideEffect.ShowToast("알람이 울릴 위치을 설정해주세요.")
                    })
                    return
                }
                if (currentState.range == null) {
                    sendEffect({
                        AddAlarmContract.AddAlarmSideEffect.ShowToast("알람이 울릴 위치 반경을 설정해주세요.")
                    })
                    return
                }
                if (currentState.way == null) {
                    sendEffect({
                        AddAlarmContract.AddAlarmSideEffect.ShowToast("위치 알람이 울릴 방법을 설정해주세요.")
                    })
                    return
                }
                val leftTimeHour: Int = currentState.leftCalendar!!.get(Calendar.HOUR_OF_DAY)
                val leftTImeMinute: Int = currentState.leftCalendar!!.get(Calendar.MINUTE)
                val rightTimeHour: Int = currentState.rightCalendar!!.get(Calendar.HOUR_OF_DAY)
                val rightTimeMinute: Int = currentState.rightCalendar!!.get(Calendar.MINUTE)
                val address: String = currentState.nameLocation.name
                val latitude: Double = currentState.nameLocation.latitude
                val longitude: Double = currentState.nameLocation.longitude
                val range: Int = currentState.range!!.value
                val way: Int = currentState.way!!.id
                insertAlarm(
                    leftTimeHour,
                    leftTImeMinute,
                    rightTimeHour,
                    rightTimeMinute,
                    address,
                    latitude,
                    longitude,
                    range,
                    way
                )


            }
        }
    }

    private fun insertAlarm(
        leftTimeHour: Int,
        leftTImeMinute: Int,
        rightTimeHour: Int,
        rightTimeMinute: Int,
        address: String,
        latitude: Double,
        longitude: Double,
        range: Int,
        way: Int
    ) {
        viewModelScope.launch {
            alarmRepository.insert(
                leftTimeHour, leftTImeMinute, rightTimeHour, rightTimeMinute, address, latitude, longitude, range, way
            ).collect {
                sendEffect({
                    AddAlarmContract.AddAlarmSideEffect.GoToBack
                })
            }
        }
    }

    private fun compareCalendar(leftCalendar: Calendar?, rightCalendar: Calendar?) {
        if (leftCalendar == null)
            return
        if (rightCalendar == null)
            return
        val result = leftCalendar.compareTo(rightCalendar)
        if (result == 1) {
            updateState {
                copy(
                    leftCalendar = rightCalendar,
                    rightCalendar = leftCalendar,
                    leftTimeText = "${
                        String.format(
                            "%02d",
                            rightCalendar.get(Calendar.HOUR_OF_DAY)
                        )
                    }:${String.format("%02d", rightCalendar.get(Calendar.MINUTE))}",
                    rightTimeText = "${
                        String.format(
                            "%02d",
                            leftCalendar.get(Calendar.HOUR_OF_DAY)
                        )
                    }:${String.format("%02d", leftCalendar.get(Calendar.MINUTE))}",
                )
            }
        }

    }
}
