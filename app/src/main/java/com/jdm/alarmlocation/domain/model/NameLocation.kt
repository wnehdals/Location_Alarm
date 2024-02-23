package com.jdm.alarmlocation.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NameLocation(
    val id: Long = -1,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
): Parcelable

