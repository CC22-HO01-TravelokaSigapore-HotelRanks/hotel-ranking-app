package com.c22ho01.hotelranking.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.CardAdapter
import com.c22ho01.hotelranking.adapter.CardLocationAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.FragmentHomeGuestBinding
import com.c22ho01.hotelranking.ui.auth.AuthActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeGuestFragment : Fragment() {

    private var _binding: FragmentHomeGuestBinding? = null
    private val binding get() = _binding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var cardAdapter: CardAdapter
    private lateinit var trendingAdapter: CardAdapter
    private lateinit var locationAdapter: CardLocationAdapter
    private lateinit var factory: ViewModelFactory
    private val homeViewModel: HomeViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeGuestBinding.inflate(inflater, container, false)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        factory = ViewModelFactory.getInstance(requireContext())
        cardAdapter = CardAdapter()
        trendingAdapter = CardAdapter()
        locationAdapter = CardLocationAdapter()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMyLastLocation()

        // card top-rated
        binding?.rvTopRated?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = cardAdapter
            setHasFixedSize(true)
            addItemDecoration(CardAdapter.MarginItemDecoration(48))
        }

        homeViewModel.getFiveStar.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    //loading
                }
                is Result.Success -> {
                    val data = it.data.data
                    cardAdapter.submitList(data)
                }
                else -> {
                    //error message
                }
            }
        }

        // card trending
        binding?.rvTrending?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = trendingAdapter
            setHasFixedSize(true)
            addItemDecoration(CardAdapter.MarginItemDecoration(48))
        }

        homeViewModel.getTrending.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    //loading
                }
                is Result.Success -> {
                    val data = it.data.data
                    trendingAdapter.submitList(data)
                }
                else -> {
                    //error message
                }
            }
        }

        binding?.rvNearLocation?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = locationAdapter
            setHasFixedSize(true)
            addItemDecoration(CardLocationAdapter.MarginItemDecoration(48))
        }

        homeViewModel.getAll.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    //loading
                }
                is Result.Success -> {
                    val data = it.data.data
                    locationAdapter.submitList(data)
                }
                else -> {
                    //error message
                }
            }
        }

        binding?.loginHereBtn?.setOnClickListener {
            Intent(activity, AuthActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    USER_LAT = location.latitude
                    USER_LONG = location.longitude
                } else {
                    // no location found
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {
                // no location granted
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var USER_LAT: Double? = null
        var USER_LONG: Double? = null
    }
}