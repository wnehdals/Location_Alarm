package com.jdm.alarmlocation.data.resp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
open class BaseGeoResp {
    @SerialName("status")
    val status: GeoStatus = GeoStatus(0, "", "")
    @SerialName("results")
    val results: List<GeoResp> = emptyList()
}

@Serializable
data class GeoStatus(
    val code: Int,
    val name: String,
    val message: String
)

@Serializable
data class GeoResp (
    @SerialName("name")
    val name: String,
    @SerialName("region")
    val region: RegionResp,
)

@Serializable
data class RegionResp(
    @SerialName("area1")
    val area1: AreaResp?,
    @SerialName("area2")
    val area2: AreaResp?,
    @SerialName("area3")
    val area3: AreaResp?,
    @SerialName("area4")
    val area4: AreaResp?,
)

@Serializable
data class AreaResp(
    @SerialName("name")
    val name: String,
)