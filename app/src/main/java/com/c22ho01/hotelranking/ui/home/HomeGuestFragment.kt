package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.CardAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.FragmentHomeGuestBinding
import com.c22ho01.hotelranking.ui.auth.AuthActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel

class HomeGuestFragment : Fragment() {

    private var _binding: FragmentHomeGuestBinding? = null
    private val binding get() = _binding
    private lateinit var topRatedAdapter: CardAdapter
    private lateinit var trendingAdapter: CardAdapter
    private lateinit var factory: ViewModelFactory
    private val homeViewModel: HomeViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeGuestBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        topRatedAdapter = CardAdapter()
        trendingAdapter = CardAdapter()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // card top-rated
        binding?.rvTopRated?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = topRatedAdapter
            setHasFixedSize(true)
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

        // card trending
        binding?.rvTrending?.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = trendingAdapter
            setHasFixedSize(true)
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

        binding?.loginHereBtn?.setOnClickListener {
            Intent(activity, AuthActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}