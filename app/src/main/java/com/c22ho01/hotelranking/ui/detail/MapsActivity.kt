package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var hotel: HotelData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        hotel = intent.getParcelableExtra<HotelData>(DetailActivity.EXTRA_HOTEL) as HotelData
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val hotelLocation = LatLng(hotel.latitude, hotel.longitude)
        mMap.addMarker(MarkerOptions().position(hotelLocation).title(hotel.name))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, 5f))
    }
}