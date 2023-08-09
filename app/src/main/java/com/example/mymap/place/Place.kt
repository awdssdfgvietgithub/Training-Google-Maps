package com.example.mymap.place

import com.google.android.gms.maps.model.LatLng

data class Place(val name: String, val rating: Float, val latLng: LatLng, val address: String)
