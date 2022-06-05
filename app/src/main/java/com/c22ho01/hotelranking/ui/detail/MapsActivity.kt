package com.c22ho01.hotelranking.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val hotel = intent.getParcelableExtra<HotelData>(DetailActivity.EXTRA_HOTEL) as HotelData
        // Add a marker in Sydney and move the camera
        val hotelLocation = LatLng(hotel.latitude, hotel.longitude)
        mMap.addMarker(MarkerOptions().position(hotelLocation).title(hotel.name))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, 15f))
    }
}