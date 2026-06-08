package com.jdm.alarmlocation.data

import com.jdm.alarmlocation.data.resp.SearchResp
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeoApi {
    @GET("/gc")
    suspend fun getSearchPlace(@Query("coords") query: String): ApiResponse<SearchResp>
}