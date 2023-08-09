package com.example.mymap.place

import android.content.Context
import com.example.mymap.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

class PlacesReader(val context: Context) {
    private val gson = Gson()

    private val inputStream: InputStream
        get() = context.resources.openRawResource(R.raw.places)

    fun read(): List<Place> {
        val itemType = object : TypeToken<List<PlacesResponse>>() {}.type
        val reader = InputStreamReader(inputStream)
        return gson.fromJson<List<PlacesResponse>>(reader, itemType).map {
            it.toPlace()
        }
    }
}