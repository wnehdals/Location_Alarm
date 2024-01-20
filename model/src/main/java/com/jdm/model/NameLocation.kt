package com.jdm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NameLocation(
    val id: Int = -1,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
): Parcelable
