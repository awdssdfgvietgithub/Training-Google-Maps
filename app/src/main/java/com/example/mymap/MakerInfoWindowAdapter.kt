package com.example.mymap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.mymap.place.Place
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MakerInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        // 1. Get tag
        val place = marker.tag as? Place ?: return null

        // 2. Inflate view and set title, address, and rating
        val view = LayoutInflater.from(context).inflate(
            R.layout.maker_info_contents, null
        )
        view.findViewById<TextView>(
            R.id.text_maker_title
        ).text = place.name
        view.findViewById<TextView>(
            R.id.text_maker_address
        ).text = place.address
        view.findViewById<TextView>(
            R.id.text_maker_rating
        ).text = "Rating: %.2f".format(place.rating)

        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}