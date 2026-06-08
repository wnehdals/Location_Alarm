package com.jdm.alarmlocation.presentation.ui.location

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jdm.alarmlocation.base.BaseViewModel
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.repository.SearchRepository
import com.jdm.alarmlocation.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    val currentPlace = SingleLiveEvent<Place>()
    val placeData = SingleLiveEvent<List<Place>>()
    val rangeData = SingleLiveEvent<Int>()
    val isInData = SingleLiveEvent<Boolean>()
    val _mapReadyFlow = MutableStateFlow(false)
    val mapReadyFlow: SharedFlow<Boolean> get() = _mapReadyFlow.asSharedFlow()
    val _alarmFlow = MutableStateFlow<Place?>(null)
    val alarmFlow: SharedFlow<Place?> get() = _alarmFlow.asSharedFlow()



    init {
        mapReadyFlow.zip(alarmFlow) { mapReady, alarm ->
            alarm
        }.onEach { alarm ->
            alarm?.let {
                currentPlace.value = it
                Log.e("SearchLocationViewModel alarm : ", it.toString())
            }
        }.catch {

        }.launchIn(viewModelScope)

    }

    fun emitAlarm(place: Place?) {
        if (place == null) return

        viewModelScope.launch {
            _alarmFlow.tryEmit(place)
        }
    }

    fun emitMapReadyFlow() {
        _mapReadyFlow.tryEmit(true)
    }

    fun searchKeyword(keyword: String) {
        searchRepository.getSearchPlace(
            query = keyword,
            onError = {
                toastMsg.value = it
            })
            .onEach {
                placeData.value = it
            }.catch {

            }.launchIn(viewModelScope)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, SearchLocationActivity::class.java)
            return intent
        }
    }

    /*

    fun getAddress() {
        try {
            if (location == null) {
                searchResultMsgData.value = "현재 위치를 찾을 수 없습니다."
                return
            }
            val geocoder =
                Geocoder(context, Locale.KOREA).getFromLocation(location!!.lat, location!!.long, 1)
            if (geocoder == null) {
                searchResultMsgData.value = "현재 위치를 찾을 수 없습니다."
                return
            }
            val address = geocoder[0].getAddressLine(0)


                insertLocation(
                    name = address,
                    latitude = location!!.lat,
                    longitude = location!!.long
                )

            selectedNameLocation = NameLocation(name = address, latitude = location!!.lat, longitude = location!!.long)
            direction.value = "finish"



        } catch (e: Exception) {
            searchResultMsgData.value = "현재 위치를 찾을 수 없습니다."
            e.printStackTrace()

        }
    }

     */

}
