package com.jdm.alarmlocation.domain

import com.jdm.alarmlocation.data.entity.AlarmEntity
import com.jdm.alarmlocation.data.entity.RoutineEntity
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.model.Routine
import com.naver.maps.geometry.LatLng
import java.util.Calendar
import kotlin.collections.toIntArray

fun LatLng.toPlace(address: String = "", title: String = ""): Place {
    val mapy = this.latitude * 10000000
    val mapx = this.longitude * 10000000
    return Place(
        mapx = mapx.toString(),
        mapy = mapy.toString(),
        roadAddress = address,
        title = title
    )
}

/**
 * latitude == mapy
 * longitude == mapx
 */
fun Place.toLatLng(): LatLng {
    return LatLng(this.mapy.toDouble() / 10000000, this.mapx.toDouble() / 10000000)
}

fun Alarm.toPlace(): Place {
    val mapy = this.latitude * 10000000
    val mapx = this.longitude * 10000000
    return Place(
        title = this.placeTitle,
        mapx = mapx.toString(),
        mapy = mapy.toString(),
    )
}

fun AlarmEntity.toDomain(): Alarm {
    return Alarm(
        id = id,
        routineId = routineId,
        placeTitle = placeTitle,
        latitude = latitude,
        longitude = longitude,
        range = range,
        isIn = isIn,
        day = day,
        hour = hour,
        minute = minute
    )
}
fun Alarm.toAlarmEntity(id: Long? = null): AlarmEntity {
    val temp =  AlarmEntity(
        routineId = routineId,
        placeTitle = placeTitle,
        latitude = latitude,
        longitude = longitude,
        range = range,
        isIn = isIn,
        day = day!!,
        hour = hour,
        minute = minute
    )
    id?.let { temp.id = it }
    return temp
}
fun Int.toDayOfKor(): String {
    return if (this == Calendar.MONDAY) {
        "월"
    } else if (this == Calendar.TUESDAY) {
        "화"
    } else if (this == Calendar.WEDNESDAY) {
        "수"
    } else if (this == Calendar.THURSDAY) {
        "목"
    } else if (this == Calendar.FRIDAY) {
        "금"
    } else if (this == Calendar.SATURDAY) {
        "토"
    } else {
        "일"
    }
}

fun RoutineEntity.toDomain(alarms: List<AlarmEntity>): Routine {
    if (alarms.isEmpty()) {
        return Routine(day = intArrayOf(), alarmIds = longArrayOf())
    }
    val routine = Routine(
        id = id,
        placeTitle = alarms[0].placeTitle,
        latitude = alarms[0].latitude,
        longitude = alarms[0].longitude,
        range = alarms[0].range,
        isIn = alarms[0].isIn,
        isOn = isOn,
        day = alarms.map { it.day }.toIntArray(),
        alarmIds = alarms.map { it.id }.toLongArray()
    )
    return routine
}

fun Routine.toData(): RoutineEntity {
    val routineEntity = RoutineEntity(
        isOn = this.isOn
    )
    routineEntity.id = this.id
    return routineEntity
}