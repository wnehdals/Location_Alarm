package com.jdm.alarmlocation.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import kotlinx.serialization.json.Json

class IntListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromIntArray(value: IntArray?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toIntArray(value: String?): IntArray? {
        return gson.fromJson(value, IntArray::class.java)
    }
}