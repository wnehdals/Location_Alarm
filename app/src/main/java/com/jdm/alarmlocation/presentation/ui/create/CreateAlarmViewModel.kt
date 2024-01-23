package com.jdm.alarmlocation.presentation.ui.create

import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreateAlarmViewModel @Inject constructor(

): BaseViewModel(){
    val leftCalendar = SingleLiveEvent<Calendar?>(null)
    val rightCalendar = SingleLiveEvent<Calendar?>(null)
    val nameLocationData = SingleLiveEvent<NameLocation?>(null)
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
}