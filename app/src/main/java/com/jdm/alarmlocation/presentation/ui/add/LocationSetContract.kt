package com.jdm.alarmlocation.presentation.ui.add

import com.jdm.core.base.ViewEvent
import com.jdm.core.base.ViewSideEffect
import com.jdm.core.base.ViewState
import com.jdm.model.NameLocation
import java.util.Objects

sealed class LocationSearchState {
    object Undefine: LocationSearchState()
    data class Loading(
        val isShowPermissionDialog: Boolean = false
    ): LocationSearchState()
    data class Success(
        val address: String
    ): LocationSearchState()

    object Fail: LocationSearchState()
}
class LocationSetContract {

    data class LocationSetState(
        val locationSearchState: LocationSearchState = LocationSearchState.Undefine,
        val locationList: List<NameLocation> = listOf(),
        val isShowPermissionDialog: Boolean = false
    ): ViewState



    sealed class LocationSetSideEffect: ViewSideEffect {
        object RequestPermission: LocationSetSideEffect()
        object SendActivityResult: LocationSetSideEffect()
    }
    sealed class LocationSetEvent: ViewEvent {
        data class SetDialogVisibility(
            val isShow: Boolean
        ): LocationSetEvent()
        object SearchLocationEvent: LocationSetEvent()
        data class OnClickRecentlyLocation(
            val nameLocation: NameLocation
        ): LocationSetEvent()
    }
}