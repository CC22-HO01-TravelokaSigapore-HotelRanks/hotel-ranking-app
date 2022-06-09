package com.c22ho01.hotelranking.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.CardReviewAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityDetail2Binding
import com.c22ho01.hotelranking.databinding.SheetPostReviewBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.review.ReviewViewModel
import com.c22ho01.hotelranking.viewmodel.utils.dpToPx
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetail2Binding
    private val imageList = ArrayList<SlideModel>()
    private lateinit var hotel: HotelData
    private lateinit var factory: ViewModelFactory
    private val reviewViewModel: ReviewViewModel by viewModels { factory }
    private lateinit var cardReviewAdapter: CardReviewAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetail2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        factory = ViewModelFactory.getInstance(this)

        cardReviewAdapter = CardReviewAdapter()

        hotel = intent.getParcelableExtra<HotelData>(EXTRA_HOTEL) as HotelData

        binding.reviewCard.setOnClickListener {
            val intent = Intent(this, ListReviewActivity::class.java)
            intent.putExtra(EXTRA_HOTEL, hotel)
            startActivity(intent)
        }

        binding.btnPost.setOnClickListener {
            openBottomSheet()
        }

        binding.topAppBar.apply {
            title = hotel.name
            setNavigationOnClickListener {
                finish()
            }
        }

        setData()
    }

    private fun openBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(
            this@DetailActivity, R.style.BottomSheetDialogTheme
        )

        val bottomSheetBinding = SheetPostReviewBinding.inflate(LayoutInflater.from(this))
        bottomSheetBinding.button.setOnClickListener {
            val text = bottomSheetBinding.tvComment.text.toString().trim()
            val rating = bottomSheetBinding.rbPost.rating.roundToInt()


            if (text.isEmpty()) {
                Toast.makeText(this@DetailActivity, "Please enter your comment", Toast.LENGTH_LONG).show()
            } else {
                postReview(text , rating)
                //Toast.makeText(this@DetailActivity, text, Toast.LENGTH_LONG).show()
            }
        }
        bottomSheetDialog.setContentView(bottomSheetBinding.bottomSheet)
        bottomSheetDialog.show()
    }

    private fun postReview(text: String, rating: Int) {
        reviewViewModel.postReview(
            "",
            hotel.id,
            128,
            text,
            rating
        ).observe(this) {
            when (it) {
                is Result.Loading -> {
                    //loading
                }
                is Result.Success -> {
                    val data = it.data.message
                    Toast.makeText(this@DetailActivity, data, Toast.LENGTH_LONG).show()
                    bottomSheetDialog.dismiss()
                }
                is Result.Error -> {
                    val toast = Toast.makeText(this, it.error, Toast.LENGTH_LONG)
                    Log.d("PostError", it.error)
                    toast.show()
                }
            }
        }
    }

    private fun setImage(images: List<String>) {
        for ((i, image) in images.withIndex()) {
            if (i == 0) imageList.add(SlideModel(image)) else imageList.add(
                SlideModel(
                    image.substring(
                        1
                    )
                )
            )
        }
        binding.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
    }

    private fun setData() {
        getReviews()
        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
            addItemDecoration(CardReviewAdapter.MarginItemDecoration(16.dpToPx))
        }



        setImage(hotel.image)
        binding.tvHotelName.text = hotel.name
        binding.tvLocation.text = hotel.neighborhood
        binding.tvRating.text = resources.getString(R.string.rating, hotel.star.toString())

        setFeatures()
    }

    private fun setFeatures() {
        if (!hotel.airConditioner) binding.layoutFacilities.removeView(binding.iconAirConditioner)
        if (!hotel.averageBedSize) binding.layoutFacilities.removeView(binding.iconAverageBedSize)
        if (!hotel.breakfast) binding.layoutFacilities.removeView(binding.iconBreakfast)
        if (!hotel.parking) binding.layoutFacilities.removeView(binding.iconParking)
        if (!hotel.childArea) binding.layoutFacilities.removeView(binding.iconChildArea)
        if (!hotel.smoking) binding.layoutFacilities.removeView(binding.iconSmoking)
        if (!hotel.pool) binding.layoutFacilities.removeView(binding.iconPool)
        if (!hotel.wheelChairAccess) binding.layoutFacilities.removeView(binding.iconWheelchairAccess)
        if (!hotel.freeRefund) binding.layoutFacilities.removeView(binding.iconFreeRefund)
        if (!hotel.staffVaccinated) binding.layoutFacilities.removeView(binding.iconStaffVaccinated)
        if (!hotel.wifi) binding.layoutFacilities.removeView(binding.iconWifi)
    }

    private fun getReviews() {
        reviewViewModel.getHotelReviews(hotel.id).observe(this) {
            when (it) {
                is Result.Loading -> {
                    //loading
                }
                is Result.Success -> {
                    val data = it.data.data
                    cardReviewAdapter.submitList(data)
                    binding.rvReview.adapter = cardReviewAdapter

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