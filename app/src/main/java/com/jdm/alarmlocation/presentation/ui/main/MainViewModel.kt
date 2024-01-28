package com.jdm.alarmlocation.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : BaseViewModel() {
    val alarmListData = SingleLiveEvent<List<Alarm>>()
    val updateAlarmData  = SingleLiveEvent<Int>()
    fun getAlarmList() {
        viewModelScope.launch {
            alarmListData.value = alarmRepository.getAllAlarm()
        }
    }

    fun updateAlarm(alarm: Alarm?) {
        if (alarm == null)
            return
        viewModelScope.launch {
            updateAlarmData.value = alarmRepository.updateAlarm(alarm)
            getAlarmList()
        }
    }
}