package com.jdm.alarmlocation.data.entity

import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.NameLocation

fun LocationEntity.toNameLocation(): NameLocation {
    return NameLocation(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}
fun AlarmEntity.toAlarm(): Alarm {
    return Alarm(
        id, leftTimeHour, leftTImeMinute, rightTimeHour, rightTimeMinute, address, latitude, longitude, range, way, isOn
    )
}
fun Alarm.toAlarmEntity(): AlarmEntity {
    val temp =  AlarmEntity(
        leftTimeHour, leftTImeMinute, rightTimeHour, rightTimeMinute, address, latitude, longitude, range, way, isOn
    )
    temp.id = id
    return temp
}