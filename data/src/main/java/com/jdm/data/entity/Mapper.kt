package com.jdm.data.entity

import com.jdm.model.NameLocation

fun LocationEntity.toNameLocation(): NameLocation {
    return NameLocation(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude
    )
}