package com.jdm.alarmlocation.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.BuildConfig
import com.jdm.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.jdm.alarmlocation.presentation.ui.splash.SplashContract.SplashSideEffect.ForceVersionUpdate
import com.jdm.alarmlocation.presentation.ui.splash.SplashContract.SplashSideEffect.MoveMain
import com.jdm.alarmlocation.presentation.ui.splash.SplashContract.SplashSideEffect.SelectVersionUpdate
import com.jdm.alarmlocation.presentation.ui.splash.SplashContract.SplashSideEffect
import com.jdm.alarmlocation.presentation.ui.splash.SplashContract.SplashEvent
import com.jdm.alarmlocation.presentation.ui.splash.SplashContract.SplashViewState
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() :
    BaseViewModel<SplashViewState, SplashSideEffect, SplashEvent>(
        SplashContract.SplashViewState.Undefine
    ) {
    override fun handleEvents(event: SplashContract.SplashEvent) {
    }

    fun getAppVersion() {
        viewModelScope.launch {
            val localVersion = BuildConfig.VERSION_NAME
            val minVersion = "0.0.0"
            val latestVersion = "0.0.1"
            if (compareVersion(localVersion, minVersion)) {
                sendEffect({
                    ForceVersionUpdate
                })

            } else if (!compareVersion(localVersion, minVersion) && compareVersion(localVersion, latestVersion)) {
                sendEffect({
                    SelectVersionUpdate
                })
            } else {
                sendEffect({
                    MoveMain
                })
            }
        }
    }

    private fun compareVersion(currentVersion: String, otherVersion: String): Boolean {
        val currentVersionIntList = currentVersion.split(".").map { it.toInt() }
        val otherVersionIntList = otherVersion.split(".").map { it.toInt() }
        mutableListOf<Int>().apply {
            currentVersionIntList.forEachIndexed { index, i ->
                add(i - (otherVersionIntList.getOrNull(index) ?: 0))
            }
        }.forEach {
            if (it > 0) {
                return false
            } else if (it < 0) {
                return true
            }
        }
        return false
    }
}