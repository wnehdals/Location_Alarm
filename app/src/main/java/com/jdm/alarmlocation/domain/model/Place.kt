package com.jdm.alarmlocation.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val title: String = "",
    val mapx: String = "1269783881",
    val mapy: String = "375666102",
    val roadAddress: String = "",
    val category: String = "",
): Parcelable