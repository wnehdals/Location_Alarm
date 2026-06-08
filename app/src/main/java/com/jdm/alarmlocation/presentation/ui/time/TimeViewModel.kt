package com.jdm.alarmlocation.presentation.ui.time

import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(

): BaseViewModel() {
    val selectedDayData = SingleLiveEvent<List<Int>>()
    val hourData = SingleLiveEvent<Int>()
    val minuteData = SingleLiveEvent<Int>()

    fun onClickDay(idx: Int) {
        val notNullList = selectedDayData.value ?: emptyList()
        val mutableList = notNullList.toMutableList()
        if (mutableList.contains(idx)) {
            mutableList.remove(idx)
        } else {
            mutableList.add(idx)
        }
        selectedDayData.value = mutableList.toList()
    }
}