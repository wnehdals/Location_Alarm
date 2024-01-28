package com.jdm.alarmlocation.presentation.ui.create

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.data.repository.AlarmRepositoryImpl
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.domain.model.Range
import com.jdm.alarmlocation.domain.model.Way
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreateAlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
): BaseViewModel(){
    val leftCalendar = SingleLiveEvent<Calendar?>(null)
    val rightCalendar = SingleLiveEvent<Calendar?>(null)
    val nameLocationData = SingleLiveEvent<NameLocation?>(null)
    val range = SingleLiveEvent<Range?>(null)
    val way = SingleLiveEvent<Way>(null)

    val insertResult = SingleLiveEvent<Long>()

    fun selectLeftTime(calendar: Calendar) {
        leftCalendar.value = calendar
        compareCalendar()
    }
    fun selectRightTime(calendar: Calendar) {
        rightCalendar.value = calendar
        compareCalendar()
    }
    private fun compareCalendar() {
        if (leftCalendar.value == null) return
        if (rightCalendar.value == null) return
        var lc = leftCalendar.value!!
        var rc = rightCalendar.value!!
        var result = lc.compareTo(rc)
        if (result == 1) {
            leftCalendar.value = rc
            rightCalendar.value = lc
        }
    }
    fun createAlarm() {
        if (leftCalendar.value == null) {
            dialogMsg.value = "시간을 선택해주세요"
            return
        }
        if (rightCalendar.value == null) {
            dialogMsg.value = "시간을 선택해주세요"
            return
        }
        if (nameLocationData.value == null) {
            dialogMsg.value = "위치를 선택해주세요"
            return
        }
        if (range.value == null) {
            dialogMsg.value = "범위를 선택해주세요"
            return
        }
        if (way.value == null) {
            dialogMsg.value = "방법을 선택해주세요"
            return
        }
        viewModelScope.launch {
            val result = alarmRepository.insert(
                leftTimeHour = leftCalendar.value!!.get(Calendar.HOUR_OF_DAY),
                leftTImeMinute = leftCalendar.value!!.get(Calendar.MINUTE),
                rightTimeHour = rightCalendar.value!!.get(Calendar.HOUR_OF_DAY),
                rightTimeMinute = rightCalendar.value!!.get(Calendar.MINUTE),
                address = nameLocationData.value!!.name,
                latitude = nameLocationData.value!!.latitude,
                longitude = nameLocationData.value!!.longitude,
                range = range.value!!.id,
                way = way.value!!.id,
                isOn = false
            )
            insertResult.value = result

        }

    }
}