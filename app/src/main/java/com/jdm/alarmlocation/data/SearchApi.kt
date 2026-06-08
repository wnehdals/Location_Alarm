package com.jdm.alarmlocation.data

import com.jdm.alarmlocation.data.resp.SearchResp
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("/v1/search/local.json")
    suspend fun getSearchPlace(@Query("query") query: String, @Query("display") display: Int): ApiResponse<SearchResp>
}