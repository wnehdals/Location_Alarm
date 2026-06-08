package com.jdm.alarmlocation.data.repository

import androidx.core.text.parseAsHtml
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jdm.alarmlocation.BuildConfig
import com.jdm.alarmlocation.data.SearchApi
import com.jdm.alarmlocation.data.di.SEARCH
import com.jdm.alarmlocation.data.resp.SearchBaseResp
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.repository.SearchRepository
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import com.skydoves.sandwich.retrofit.errorBody
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject
import okhttp3.Interceptor

class SearchRepositoryImpl @Inject constructor(
    @SEARCH private val searchApi: SearchApi
) : SearchRepository {
    override fun getSearchPlace(query: String, onError: (String) -> Unit): Flow<List<Place>> =
        flow {
            searchApi.getSearchPlace(query, 10)
                .suspendOnSuccess {
                    val list = data.items.map {
                        val notNulltitle = it.title?: ""
                        Place(
                            title = notNulltitle.parseAsHtml().toString(),
                            mapx = it.mapx ?: "",
                            mapy = it.mapy ?: "",
                            roadAddress = it.roadAddress ?: "",
                            category = it.category?: "",
                        )
                    }
                    emit(list)
                }.onError {
                    val jsonStr = this.errorBody?.charStream()!!
                    var jsonObject = Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<SearchBaseResp>(jsonStr.readText())
                    onError("${jsonObject.errorMessage}")
                }.onException {
                    onError("${this.message}")
                }
        }
}