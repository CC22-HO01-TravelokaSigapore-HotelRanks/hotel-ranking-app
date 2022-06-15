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
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.CardAdapter
import com.c22ho01.hotelranking.adapter.CardLocationAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.databinding.FragmentHomeLoggedInBinding
import com.c22ho01.hotelranking.ui.foryou.ForYouActivity
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.utils.dpToPx
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.*

class HomeLoggedInFragment : Fragment() {

    private var _binding: FragmentHomeLoggedInBinding? = null
    private val binding get() = _binding
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var topRatedAdapter: CardAdapter
    private lateinit var trendingAdapter: CardAdapter
    private lateinit var locationAdapter: CardLocationAdapter
    private lateinit var userRecommendationAdapter: CardAdapter
    private lateinit var factory: ViewModelFactory
    private val homeViewModel by viewModels<HomeViewModel> { factory }
    private val profileViewModel by viewModels<ProfileViewModel> { factory }
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
        userRecommendationAdapter = CardAdapter()
        userLocation = UserLocation(
            userId = profileViewModel.getProfileID() ?: -1,
        )

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMyLastLocation()
        setupAction()
        getTopRated()
        getTrending()
    }

    private fun setupAction() {
        profileViewModel.loadProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    profileViewModel.getCurrentProfile().observe(viewLifecycleOwner) { profile ->
                        binding?.apply {
                            tvName.text =
                                requireActivity().resources.getString(R.string.name, profile?.name)
                            cardProfileCustomization.isVisible = profile?.name == null

                            val review = profile?.reviewCounter ?: 0
                            tvCounter.text = requireActivity().resources.getString(
                                R.string.review_counter,
                                review
                            )

                            reviewIndicator.progress = review.times(10)
                            if (review < 10) {
                                reviewIndicator.isVisible = true
                                btnForYou.isEnabled = false
                                btnForYou.isClickable = false
                            }

                            getUserRecommendation(profile?.id ?: -1)

                            btnEditProfile.setOnClickListener {
                                startActivity(
                                    Intent(activity, ProfileCustomizeActivity::class.java)
                                        .putExtra(ProfileCustomizeActivity.EXTRA_PROFILE, profile)
                                )
                            }
                        }
                    }
                }
                else -> {}
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
                startActivity(
                    Intent(activity, ForYouActivity::class.java).also {
                        it.putExtra(ForYouActivity.USER_LOCATION_EXTRA, userLocation)
                    }
                )
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

    private fun getUserRecommendation(userId: Int) {
        binding?.rvUserRecommendation?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = userRecommendationAdapter
            setHasFixedSize(true)
            addItemDecoration(CardAdapter.MarginItemDecoration(16.dpToPx))
        }

        homeViewModel.getUserRecommendation(profileViewModel.userToken, userId)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> {
                        binding?.rvUserRecommendation?.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding?.apply {
                            shimmerUserRecommendation.stopShimmer()
                            shimmerUserRecommendation.visibility = View.GONE
                            rvUserRecommendation.visibility = View.VISIBLE
                        }
                        val data = it.data.data
                        userRecommendationAdapter.submitList(data)
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
            var isExecuted = false
            val cts = CancellationTokenSource()
            fusedLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location: Location? ->
                    if (isExecuted) return@addOnSuccessListener
                    isExecuted = true
                    if (location != null) {
                        USER_LAT = location.latitude
                        USER_LONG = location.longitude
                        userLocation = userLocation.copy(
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

                        homeViewModel.getLocation(profileViewModel.userToken, userLocation)
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
    }
}