package com.c22ho01.hotelranking.ui.home

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.CardAdapter
import com.c22ho01.hotelranking.adapter.CardLocationAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.databinding.FragmentHomeLoggedInBinding
import com.c22ho01.hotelranking.ui.foryou.ForYouActivity
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeLoggedInFragment : Fragment() {

    private var _binding: FragmentHomeLoggedInBinding? = null
    private val binding get() = _binding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var topRatedAdapter: CardAdapter
    private lateinit var trendingAdapter: CardAdapter
    private lateinit var locationAdapter: CardLocationAdapter
    private lateinit var factory: ViewModelFactory
    private val homeViewModel: HomeViewModel by viewModels { factory }
    private val profileViewModel: ProfileViewModel by viewModels { factory }
    private lateinit var userLocation: UserLocation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeLoggedInBinding.inflate(inflater, container, false)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        factory = ViewModelFactory.getInstance(requireContext())
        topRatedAdapter = CardAdapter()
        trendingAdapter = CardAdapter()
        locationAdapter = CardLocationAdapter()
        userLocation = UserLocation()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        getMyLastLocation()
        getTopRated()
        getTrending()
    }

    private fun setupAction() {
        binding?.apply {
            profileViewModel.getCurrentProfile().observe(viewLifecycleOwner) {
                cardProfileCustomization.isVisible = it.name != null
                (cardCheckItOut.layoutParams as ViewGroup.MarginLayoutParams)
                    .setMargins(48, 24, 48, 0)
            }

            btnEditProfile.setOnClickListener {
                startActivity(
                    Intent(activity, ProfileCustomizeActivity::class.java)
                )
            }

            btnMaybeLater.setOnClickListener {
                cardProfileCustomization.animate()
                    .alpha(0.0f)
                    .setDuration(1000)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            cardProfileCustomization.visibility = View.GONE
                            (cardCheckItOut.layoutParams as ViewGroup.MarginLayoutParams)
                                .setMargins(48, 24, 48, 0)
                        }
                    })
            }

            btnCheckItOut.setOnClickListener {
                scrollView.smoothScrollTo(0, tvHotelRecommendation.y.toInt())
            }

            btnForYou.setOnClickListener {
                startActivity(Intent(activity, ForYouActivity::class.java))
            }
        }
    }

    private fun getTopRated() {
        binding?.rvTopRated?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = topRatedAdapter
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
                    topRatedAdapter.submitList(data)
                }
                else -> {
                    //error message
                }
            }
        }
    }

    private fun getTrending() {
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
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    USER_LAT = location.latitude
                    USER_LONG = location.longitude
                    userLocation = UserLocation(
                        userId = profileViewModel.getProfileID(),
                        longitude = USER_LONG,
                        latitude = USER_LAT
                    )

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

                    homeViewModel.getLocation(userLocation).observe(viewLifecycleOwner) {
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