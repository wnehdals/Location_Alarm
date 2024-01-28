package com.jdm.alarmlocation.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alarm(
    val id: Long,
    val leftTimeHour: Int,
    val leftTImeMinute: Int,
    val rightTimeHour: Int,
    val rightTimeMinute: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val range: Int,
    val way: Int,        //0 - 진입하면, 1 - 벗어나면
    var isOn: Boolean
): Parcelable
