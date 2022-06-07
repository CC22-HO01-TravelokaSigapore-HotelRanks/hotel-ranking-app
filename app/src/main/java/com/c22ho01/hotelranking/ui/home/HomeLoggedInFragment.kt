package com.c22ho01.hotelranking.ui.home

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.CardAdapter
import com.c22ho01.hotelranking.adapter.CardLocationAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.data.remote.response.profile.ProfileData
import com.c22ho01.hotelranking.databinding.FragmentHomeLoggedInBinding
import com.c22ho01.hotelranking.ui.foryou.ForYouActivity
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.c22ho01.hotelranking.viewmodel.utils.dpToPx
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class HomeLoggedInFragment : Fragment() {

    private var _binding: FragmentHomeLoggedInBinding? = null
    private val binding get() = _binding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var topRatedAdapter: CardAdapter
    private lateinit var trendingAdapter: CardAdapter
    private lateinit var locationAdapter: CardLocationAdapter
    private lateinit var factory: ViewModelFactory
    private val homeViewModel by viewModels<HomeViewModel> { factory }
    private val tokenViewModel by viewModels<TokenViewModel> { factory }
    private lateinit var token: String
    private lateinit var userLocation: UserLocation
    private lateinit var userProfile: ProfileData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeLoggedInBinding.inflate(inflater, container, false)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        factory = ViewModelFactory.getInstance(requireContext())
        token = "a"
        topRatedAdapter = CardAdapter()
        trendingAdapter = CardAdapter()
        locationAdapter = CardLocationAdapter()
        userLocation = UserLocation()
        userProfile = ProfileData()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        getMyLastLocation()
        getTopRated()
        getTrending()
        getUserRecommendation()
    }

    private fun getToken() {
        lifecycleScope.launch {
            tokenViewModel.getAccessToken().collect {
                if (it != null) {
                    token = it
                }
            }
        }
    }

    private fun setupAction() {
        val userId = requireActivity().intent?.getIntExtra(USER_ID, -1)
        getToken()
        homeViewModel.getUserById("Bearer $token", userId ?: -1)
            .observe(viewLifecycleOwner) {
                if (it is Result.Success) {
                    userProfile = it.data.profileData ?: ProfileData()
                    binding?.apply {
                        tvName.text = userProfile.name
                        cardProfileCustomization.isVisible = userProfile.name == null

                        val review = userProfile.reviewCounter
                        tvCounter.text = requireActivity().resources.getString(
                            R.string.review_counter,
                            review
                        )

                        if (review != null) {
                            reviewIndicator.progress = review.times(10)
                            if (review <= 10) {
                                reviewIndicator.isVisible = true
                                btnForYou.isEnabled = false
                            }
                        }
                    }
                }
            }

        binding?.apply {
            when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 1..11 -> {
                    tvGreetings.text = requireActivity().resources.getString(R.string.morning)
                    iconWeather.setImageResource(R.drawable.ic_sunrise)
                }
                in 12..17 -> {
                    tvGreetings.text = requireActivity().resources.getString(R.string.afternoon)
                    iconWeather.setImageResource(R.drawable.ic_light_mode)
                }
                in 18..24 -> {
                    tvGreetings.text = requireActivity().resources.getString(R.string.evening)
                    iconWeather.setImageResource(R.drawable.ic_dark_mode)
                }
            }

            btnEditProfile.setOnClickListener {
                startActivity(
                    Intent(
                        activity,
                        ProfileCustomizeActivity::class.java
                    )
                        .putExtra(ProfileCustomizeActivity.EXTRA_PROFILE, userProfile)
                )
            }

            btnMaybeLater.setOnClickListener {
                cardProfileCustomization.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            cardProfileCustomization.visibility = View.GONE
                        }
                    })
            }

            btnCheckItOut.setOnClickListener {
                val target = tvHotelRecommendation.y.toInt()
                ObjectAnimator.ofInt(scrollView, "scrollY", target)
                    .setDuration(500).start()
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
            addItemDecoration(CardAdapter.MarginItemDecoration(16.dpToPx))
        }

        homeViewModel.getFiveStar.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding?.rvTopRated?.visibility = View.GONE
                }
                is Result.Success -> {
                    binding?.apply {
                        shimmerTopRated.stopShimmer()
                        shimmerTopRated.visibility = View.GONE
                        rvTopRated.visibility = View.VISIBLE
                    }
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
            addItemDecoration(CardAdapter.MarginItemDecoration(16.dpToPx))
        }

        homeViewModel.getTrending.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding?.rvTrending?.visibility = View.GONE
                }
                is Result.Success -> {
                    binding?.apply {
                        shimmerTrending.stopShimmer()
                        shimmerTrending.visibility = View.GONE
                        rvTrending.visibility = View.VISIBLE
                    }
                    val data = it.data.data
                    trendingAdapter.submitList(data)
                }
                else -> {
                    //error message
                }
            }
        }
    }

    private fun getUserRecommendation() {

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
                        addItemDecoration(CardLocationAdapter.MarginItemDecoration(16.dpToPx))
                    }

                    homeViewModel.getLocation("Bearer $token", userLocation)
                        .observe(viewLifecycleOwner) {
                            when (it) {
                                is Result.Loading -> {
                                    binding?.rvNearLocation?.visibility = View.GONE
                                }
                                is Result.Success -> {
                                    binding?.apply {
                                        shimmerLocation.stopShimmer()
                                        shimmerLocation.visibility = View.GONE
                                        rvNearLocation.visibility = View.VISIBLE
                                    }
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
        const val USER_ID = "user_id"
    }
}