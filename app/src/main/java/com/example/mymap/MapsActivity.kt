package com.example.mymap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.mymap.databinding.ActivityMapsBinding
import com.example.mymap.place.Place
import com.example.mymap.place.PlacesReader
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private var circle: Circle? = null
    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.colorPrimary)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_baseline_directions_bike_24, color)
    }

    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* 8 */
        searchViewOnClick()

        /* 1 */
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        /* 2 */
        mMap = googleMap
        mMap.setInfoWindowAdapter(MakerInfoWindowAdapter(this))

        val latitude = 10.79877246173053
        val longitude = 106.67118725596038
        val homeLatLng = LatLng(latitude, longitude)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, ZOOM.Streets.level))
        /* 6 */
        // Add an overlay
        val overlaySize = 100f //Width of overlay
        val androidOverlay =
            GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.company))
                .position(homeLatLng, overlaySize)
        mMap.addGroundOverlay(androidOverlay)

        val maker = mMap.addMarker(MarkerOptions().position(homeLatLng))

        /* 3 */
        setMapLongClip(mMap)
        /* 4 */
        setPoiClick(mMap)
        /* 5 */
        setMapStyle(mMap)
        /* 7 */
        enableMyLocation()
        /* 9 */
        addMakerForObjectFromJson(mMap)
    }

    private fun setMapLongClip(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener {
            // A snippet is additional text that's displayed after the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                it.latitude,
                it.longitude
            )
            googleMap.addMarker(
                MarkerOptions().position(it).title("S3CORP").snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
//            val latLng: LatLng = LatLng(it.latitude, it.longitude)
//            googleMap.addCircle(
//                CircleOptions()
//                    .center(latLng)
//                    .radius(1000.0)
//                    .fillColor(ContextCompat.getColor(this, R.color.colorPrimaryTranslucent))
//                    .strokeColor(ContextCompat.getColor(this, R.color.colorPrimary))
//            )
        }
        // Click on exists makers will be remove
//        googleMap.setOnMarkerClickListener {
//            it.remove()
//            true
//        }
    }

    private fun setPoiClick(googleMap: GoogleMap) {
        googleMap.setOnPoiClickListener {
            val poiMaker = googleMap.addMarker(MarkerOptions().position(it.latLng).title(it.name))
            poiMaker?.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: $e")
        }
    }

    private fun searchViewOnClick() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val location = binding.searchView.query.toString()
                var addressList: List<Address>? = null
                if (location != "") {
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address: Address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            ZOOM.Streets.level
                        )
                    )
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    private fun addMakerForObjectFromJson(googleMap: GoogleMap) {
        places.forEach() {
            val maker = googleMap.addMarker(
                MarkerOptions().position(it.latLng).title(it.name)
                    .icon(bicycleIcon)
            )
            maker?.tag = places
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_types_of_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    // Check permission granted
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}

enum class ZOOM(val level: Float) {
    World(1f),
    Landmass(5f),
    City(10f),
    Streets(15f),
    Buildings(20f);
}