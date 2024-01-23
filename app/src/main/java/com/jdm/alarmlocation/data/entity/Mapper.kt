package com.jdm.alarmlocation.data.entity

import com.jdm.alarmlocation.domain.model.NameLocation

fun LocationEntity.toNameLocation(): NameLocation {
    return NameLocation(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}