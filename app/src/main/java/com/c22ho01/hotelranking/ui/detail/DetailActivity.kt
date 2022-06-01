package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityDetail2Binding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetail2Binding? = null
    private val binding get() = _binding
    val imageList = ArrayList<SlideModel>()
    private lateinit var hotel: HotelData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetail2Binding.inflate(layoutInflater)
        setContentView(binding?.root)

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
        setImage(hotel.image)
        binding?.tvHotelName?.text = hotel.name
        binding?.tvLocation?.text = hotel.neighborhood
        binding?.tvRating?.text = hotel.star.toString()
    }

    companion object {
        const val EXTRA_HOTEL = "extra_hotel"
    }
}