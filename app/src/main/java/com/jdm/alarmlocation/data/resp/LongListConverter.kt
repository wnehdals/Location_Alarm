package com.jdm.alarmlocation.data.resp

import androidx.room.TypeConverter
import com.google.gson.Gson
import kotlinx.serialization.json.Json

class LongListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLongArray(value: LongArray?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLongArray(value: String?): LongArray? {
        return gson.fromJson(value, LongArray::class.java)
    }
}