package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.databinding.ActivityDetail2Binding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetail2Binding? = null
    private val binding get() = _binding
    val imageList = ArrayList<SlideModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetail2Binding.inflate(layoutInflater)
        setContentView(binding?.root)

        setImage()
//        val hotel = intent.getParcelableExtra<HotelEntity>(EXTRA_HOTEL) as HotelEntity


    }

    private fun setImage() {
        imageList.apply {
            add(SlideModel("https://bit.ly/2YoJ77H"))
            add(SlideModel("https://bit.ly/2BteuF2"))
            add(SlideModel("https://bit.ly/3fLJf72"))

        }
        binding?.imageSlider?.setImageList(imageList, ScaleTypes.CENTER_CROP)
    }

//    companion object {
//        const val EXTRA_HOTEL = "extra_hotel"
//    }
}