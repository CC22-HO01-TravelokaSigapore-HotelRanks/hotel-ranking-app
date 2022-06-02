package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.CardReviewAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityDetail2Binding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.review.ReviewViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetail2Binding? = null
    private val binding get() = _binding
    val imageList = ArrayList<SlideModel>()
    private lateinit var hotel: HotelData
    private lateinit var factory: ViewModelFactory
    private val reviewViewModel: ReviewViewModel by viewModels { factory }
    private lateinit var cardReviewAdapter : CardReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetail2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        factory = ViewModelFactory.getInstance(this)

        cardReviewAdapter = CardReviewAdapter()

        binding?.topAppBar?.setNavigationOnClickListener {
            finish()
        }

        hotel = intent.getParcelableExtra<HotelData>(EXTRA_HOTEL) as HotelData

        setData()
    }

    private fun setImage(images: List<String>) {
        for ((i, image) in images.withIndex()) {
            if (i == 0) imageList.add(SlideModel(image)) else imageList.add(SlideModel(image.substring(1)))
        }
        binding?.imageSlider?.setImageList(imageList, ScaleTypes.CENTER_CROP)
    }

    private fun setData() {
        getReviews()
        binding?.rvReview?.apply {
            layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
            addItemDecoration(CardReviewAdapter.MarginItemDecoration(0))
        }

        setImage(hotel.image)
        binding?.tvHotelName?.text = hotel.name
        binding?.tvLocation?.text = hotel.neighborhood
        binding?.tvRating?.text = resources.getString(R.string.rating, hotel.star.toString())


        setFeatures()
    }

    private fun setFeatures() {
        if (!hotel.airConditioner) binding?.layoutFacilities?.removeView(binding?.iconAirConditioner)
        if (!hotel.averageBedSize) binding?.layoutFacilities?.removeView(binding?.iconAverageBedSize)
        if (!hotel.breakfast) binding?.layoutFacilities?.removeView(binding?.iconBreakfast)
        if (!hotel.parking) binding?.layoutFacilities?.removeView(binding?.iconParking)
        if (!hotel.childArea) binding?.layoutFacilities?.removeView(binding?.iconChildArea)
        if (!hotel.smoking) binding?.layoutFacilities?.removeView(binding?.iconSmoking)
        if (!hotel.pool) binding?.layoutFacilities?.removeView(binding?.iconPool)
        if (!hotel.wheelChairAccess) binding?.layoutFacilities?.removeView(binding?.iconWheelchairAccess)
        if (!hotel.freeRefund) binding?.layoutFacilities?.removeView(binding?.iconFreeRefund)
        if (!hotel.staffVaccinated) binding?.layoutFacilities?.removeView(binding?.iconStaffVaccinated)
        if (!hotel.wifi) binding?.layoutFacilities?.removeView(binding?.iconWifi)
    }

    private fun getReviews() {
        reviewViewModel.getHotelReviews(hotel.id).observe(this) {
            when (it) {
                is Result.Loading -> {
                    //loading
                }
                is Result.Success -> {
                    val data = it.data.results
                    cardReviewAdapter.submitList(data)
                    binding?.rvReview?.adapter = cardReviewAdapter

                }
                is Result.Error -> {
                    val toast = Toast.makeText(this, it.error, Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_HOTEL = "extra_hotel"
    }
}