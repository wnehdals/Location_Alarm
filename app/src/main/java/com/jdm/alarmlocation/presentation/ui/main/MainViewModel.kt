package com.jdm.alarmlocation.presentation.ui.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.Routine
import com.jdm.alarmlocation.domain.repository.AlarmRepository
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : BaseViewModel() {
    val alarmListData = SingleLiveEvent<List<Routine>>()
    fun getAlarmList() {
        viewModelScope.launch {
            alarmListData.value = alarmRepository.getAllRoutine()
        }
    }
    fun updateAlarm(routine: Routine?, onRefresh: () -> Unit) {
        if (routine == null) return
        viewModelScope.launch {
            alarmRepository.updateRoutine(routine)
            getAlarmList()
            onRefresh()
        }
    }

}