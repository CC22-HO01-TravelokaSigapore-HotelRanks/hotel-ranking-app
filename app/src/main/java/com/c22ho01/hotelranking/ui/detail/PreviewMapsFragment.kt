package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.viewmodel.hotel.DetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PreviewMapsFragment : Fragment() {

    private val detailViewModel: DetailViewModel by activityViewModels()
    private val callback = OnMapReadyCallback { googleMap ->
        detailViewModel.getHotel().observe(viewLifecycleOwner) {
            val hotel = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(MarkerOptions().position(hotel).title(it.name))
            googleMap.uiSettings.isScrollGesturesEnabled = false
            googleMap.uiSettings.isZoomGesturesEnabled = false

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hotel, 15f))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preview_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}