package com.c22ho01.hotelranking.ui.foryou

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.ListForYouAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.databinding.ActivityForYouBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.ForYouViewModel
import com.google.android.material.snackbar.Snackbar

class ForYouActivity : AppCompatActivity() {
    private var _binding: ActivityForYouBinding? = null
    private val binding get() = _binding
    private lateinit var userLocation: UserLocation
    private lateinit var factory: ViewModelFactory
    private val forYouViewModel: ForYouViewModel by viewModels { factory }

    private lateinit var forYouAdapter: ListForYouAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForYouBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(this)
        userLocation = intent.getParcelableExtra(USER_LOCATION_EXTRA) ?: UserLocation()
        forYouAdapter = ListForYouAdapter()

        binding?.appbarMtForYou?.setNavigationOnClickListener {
            onBackPressed()
        }

        loadForYouHotels()
        setContentView(binding?.root)
    }

    private fun loadForYouHotels() {
        forYouViewModel.getForYouHotels(userLocation).run {
            if (this.hasObservers()) {
                this.removeObservers(this@ForYouActivity)
            }
            this.observe(this@ForYouActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        binding?.rvForYou?.run {
                            forYouAdapter.submitList(result.data.data)
                            adapter = forYouAdapter
                            layoutManager = LinearLayoutManager(this@ForYouActivity)
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        binding?.let { activityBinding ->
                            Snackbar.make(
                                activityBinding.root,
                                result.error,
                                Snackbar.LENGTH_LONG,
                            ).show()
                        }
                    }
                }

            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.run {
                sflForYou.apply {
                    startShimmer()
                    visibility = VISIBLE
                }
                rvForYou.visibility = GONE
            }
        } else {
            binding?.run {
                sflForYou.apply {
                    stopShimmer()
                    visibility = GONE
                }
                rvForYou.visibility = VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_LOCATION_EXTRA = "user_location"
    }
}