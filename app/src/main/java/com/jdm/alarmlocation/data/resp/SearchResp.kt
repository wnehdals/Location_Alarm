package com.jdm.alarmlocation.data.resp

import kotlinx.serialization.Serializable

@Serializable
data class SearchResp(
    val display: Int,
    val items: List<Item>,
    val lastBuildDate: String,
    val start: Int,
    val total: Int
): SearchBaseResp()