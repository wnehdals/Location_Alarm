package com.jdm.alarmlocation.presentation.ui.add

import com.jdm.core.base.ViewEvent
import com.jdm.core.base.ViewSideEffect
import com.jdm.core.base.ViewState
import com.jdm.model.NameLocation
import java.util.Calendar
import java.util.Objects

class AddAlarmContract {
    data class AddAlarmState(
        val leftTimeText: String = "",
        val rightTimeText: String = "",
        val leftCalendar: Calendar? = null,
        val rightCalendar: Calendar? = null,
        val nameLocation: NameLocation = NameLocation(),
        val range: Range? = null,
        val way: Way? = null
    ): ViewState



    sealed class AddAlarmSideEffect: ViewSideEffect {
        object GoToLocationSearch : AddAlarmSideEffect()
        object GoToBack : AddAlarmSideEffect()
        data class ShowToast(
            val msg: String
        ): AddAlarmSideEffect()
    }
    sealed class AddAlarmEvent: ViewEvent {
        object OnClickSearchLocation: AddAlarmEvent()
        data class SendNameLocation(
            val nameLocation: NameLocation
        ): AddAlarmEvent()
        data class OnClickLeftTimePickerConfirm(
            val calendar: Calendar
        ): AddAlarmEvent()
        data class OnClickRightTimePickerConfirm(
            val calendar: Calendar
        ): AddAlarmEvent()
        data class OnClickRangeSelect(
            val range: Range
        ): AddAlarmEvent()
        data class OnClickWay(
            val way: Way
        ): AddAlarmEvent()
        object OnClickAddAlarmLocation: AddAlarmEvent()
    }
}