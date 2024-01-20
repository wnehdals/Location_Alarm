package com.jdm.alarmlocation.presentation.ui.add

import com.jdm.core.base.ViewEvent
import com.jdm.core.base.ViewSideEffect
import com.jdm.core.base.ViewState
import java.util.Objects

class TimeSetContract {
    sealed class TimeSetState: ViewState {
        data class Loading(
            val isShowPermissionDialog: Boolean = false
        ): TimeSetState()
        object Fail: TimeSetState()
    }



    sealed class TimeSetSideEffect: ViewSideEffect {
        object MoveMain : TimeSetSideEffect()
    }
    sealed class TimeSetEvent: ViewEvent {
        data class SetDialogVisibility(
            val isShow: Boolean
        ): TimeSetEvent()
    }
}