package com.jdm.alarmlocation.data.draft

import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.LocationRoutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 알람 생성/수정 플로우의 진행 중 초안을 보관한다.
 *
 * 위치 단계(XML+MVVM [com.jdm.alarmlocation.presentation.ui.location.SearchLocationActivity])와
 * 시간·알림 단계(Compose+MVI) 사이에서 데이터를 전달하는 인메모리 브리지.
 * 단일 생성 플로우만 동시에 진행된다는 전제이므로 단순 싱글톤으로 충분하다.
 */
@Singleton
class RoutineDraftStore @Inject constructor() {

    /** 현재 편집 중인 초안. 새 생성 시작 시 [start], 위치 선택 후 [applyLocation] 으로 갱신. */
    var draft: LocationRoutine = LocationRoutine()
        private set

    fun start(existing: LocationRoutine?) {
        draft = existing ?: LocationRoutine()
    }

    fun applyLocation(
        title: String,
        address: String,
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        direction: AlarmDirection,
    ) {
        draft = draft.copy(
            title = title,
            address = address,
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            direction = direction,
        )
    }

    fun clear() {
        draft = LocationRoutine()
    }
}
