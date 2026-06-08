package com.jdm.alarmlocation.domain.repository

import com.jdm.alarmlocation.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getSearchPlace(query: String,  onError: (String) -> Unit): Flow<List<Place>>
}