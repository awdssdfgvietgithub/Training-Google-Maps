package com.example.mymap.place

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Place(val name: String, val rating: Float, val latLng: LatLng, val address: String) :
    ClusterItem {
    override fun getPosition() = latLng

    override fun getTitle() = name

    override fun getSnippet() = address
}
