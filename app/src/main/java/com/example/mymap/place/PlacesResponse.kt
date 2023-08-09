package com.example.mymap.place

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.Geometry

data class PlacesResponse(
    val geometry: Geometry,
    val name: String,
    val rating: Float,
    val vicinity: String
) {
    data class Geometry(val location: GeometryLocation)
    data class GeometryLocation(val lat: Double, val lng: Double)
}

fun PlacesResponse.toPlace(): Place =
    Place(
        name = name,
        rating = rating,
        latLng = LatLng(geometry.location.lat, geometry.location.lng),
        address = vicinity
    )