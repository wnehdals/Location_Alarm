package com.jdm.alarmlocation.data.resp

import kotlinx.serialization.Serializable

@Serializable
open class SearchBaseResp {
    val errorMessage: String? = null
    val errorCode: String? = null
}