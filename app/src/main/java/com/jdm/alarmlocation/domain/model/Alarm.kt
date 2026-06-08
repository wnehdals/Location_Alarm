package com.jdm.alarmlocation.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alarm(
    val id: Long = -1,
    val routineId: Long = -1,
    val placeTitle: String = "",
    var latitude: Double = 37.5666102,
    var longitude: Double = 126.9783881,
    var range: Int = 50,
    var isIn: Boolean = false,
    var day: Int,
    val hour: Int,
    val minute: Int
): Parcelable
