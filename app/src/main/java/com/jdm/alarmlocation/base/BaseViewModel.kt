package com.jdm.alarmlocation.base

import androidx.lifecycle.ViewModel
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent

abstract class BaseViewModel(
) : ViewModel() {
    val toastMsg = SingleLiveEvent<String>()
    val dialogMsg = SingleLiveEvent<String>()
    val isLoading = SingleLiveEvent<Boolean>()
}
