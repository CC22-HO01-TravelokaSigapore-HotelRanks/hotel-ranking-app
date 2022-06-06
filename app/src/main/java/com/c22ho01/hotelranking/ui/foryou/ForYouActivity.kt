package com.c22ho01.hotelranking.ui.foryou

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.data.remote.response.hotel.UserLocation
import com.c22ho01.hotelranking.databinding.ActivityForYouBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.ForYouViewModel

class ForYouActivity : AppCompatActivity() {
    private var _binding: ActivityForYouBinding? = null
    private val binding get() = _binding
    private var userLocation: UserLocation? = null
    private lateinit var factory: ViewModelFactory
    private lateinit var forYouViewModel: ForYouViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForYouBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(this)
        userLocation = intent.getParcelableExtra(USER_LOCATION_EXTRA)


        binding?.appbarMtForYou?.setNavigationOnClickListener {
            onBackPressed()
        }

        loadForYouHotels()
        setContentView(binding?.root)
    }

    private fun loadForYouHotels() {

    }


    fun showLoading(isLoading: Boolean) {
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