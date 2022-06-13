package com.c22ho01.hotelranking.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.CardAdapter
import com.c22ho01.hotelranking.adapter.CardReviewAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityDetail2Binding
import com.c22ho01.hotelranking.databinding.SheetPostReviewBinding
import com.c22ho01.hotelranking.utils.dpToPx
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.DetailViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.review.ReviewViewModel
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
    private val profileViewModel: ProfileViewModel by viewModels { factory }
    private val detailViewModel: DetailViewModel by viewModels { factory }
    private lateinit var cardReviewAdapter: CardReviewAdapter
    private lateinit var similaritiesAdapter: CardAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var sheetPostReviewBinding: SheetPostReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetail2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        factory = ViewModelFactory.getInstance(this)

        cardReviewAdapter = CardReviewAdapter()
        similaritiesAdapter = CardAdapter()

        hotel = intent.getParcelableExtra<HotelData>(EXTRA_HOTEL) as HotelData
        detailViewModel.setHotel(hotel)
        setMapsFragment()

        checkLoginStatus()

        binding.iconReviewDetail.setOnClickListener {
            val intent = Intent(this, ListReviewActivity::class.java)
            intent.putExtra(EXTRA_HOTEL, hotel)
            startActivity(intent)
        }

        binding.iconLocationDetail.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(EXTRA_HOTEL, hotel)
            startActivity(intent)
        }

        binding.topAppBar.apply {
            title = hotel.name
            setNavigationOnClickListener {
                finish()
            }
        }

        setData()
    }

    private fun setMapsFragment() {
        val fragmentManager = supportFragmentManager
        val previewMapsFragment = PreviewMapsFragment()
        fragmentManager.commit {
            add(
                R.id.frame_previewMaps,
                previewMapsFragment,
                PreviewMapsFragment::class.java.simpleName
            )
        }
    }

    private fun checkLoginStatus() {
        val profileId = profileViewModel.getProfileID()
        val userToken = profileViewModel.userToken
        if (profileId != null && userToken != "") {
            setSimilarities()
            binding.btnPost.setOnClickListener {
                openBottomSheet(profileId.toInt())
            }
        } else {
            binding.apply {
                btnPost.visibility = View.GONE
                similarities.visibility = View.GONE
                tvSimilarities.visibility = View.GONE
                btnSimilarities.visibility = View.GONE
            }
        }
    }

    private fun openBottomSheet(profileId: Int) {
        bottomSheetDialog = BottomSheetDialog(
            this@DetailActivity, R.style.BottomSheetDialogTheme
        )

        sheetPostReviewBinding = SheetPostReviewBinding.inflate(LayoutInflater.from(this))
        sheetPostReviewBinding.btnPost.setOnClickListener {
            val text = sheetPostReviewBinding.tvComment.text.toString().trim()
            val rating = sheetPostReviewBinding.rbPost.rating.roundToInt()


            if (text.isEmpty()) {
                Toast.makeText(this@DetailActivity, "Please enter your comment", Toast.LENGTH_LONG)
                    .show()
            } else {
                postReview(text, rating, profileId)
            }
        }
        bottomSheetDialog.setContentView(sheetPostReviewBinding.bottomSheet)
        bottomSheetDialog.show()
    }

    private fun postReview(text: String, rating: Int, profileId: Int) {
        reviewViewModel.postReview(
            profileViewModel.userToken,
            hotel.id,
            profileId,
            text,
            rating
        ).observe(this) {
            when (it) {
                is Result.Loading -> {
                    sheetPostReviewBinding.run {
                        pbPostReview.visibility = View.VISIBLE
                        btnPost.isEnabled = false
                    }
                }
                is Result.Success -> {
                    val data = it.data.message
                    val currentProfile: ProfileEntity =
                        profileViewModel.getCurrentProfile().value ?: ProfileEntity()
                    val currentReviewCounter = (currentProfile.reviewCounter ?: 0)
                    profileViewModel.setCurrentProfile(
                        currentProfile.copy(
                            reviewCounter = currentReviewCounter + 1,
                        )
                    )
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
            adapter = cardReviewAdapter
            setHasFixedSize(true)
            addItemDecoration(CardReviewAdapter.MarginItemDecoration(16.dpToPx))
        }

        setImage(hotel.image)
        binding.tvHotelName.text = hotel.name
        binding.tvLocation.text = hotel.neighborhood
        binding.tvRating.text = resources.getString(R.string.rating, hotel.star.toString())
        binding.tvPrice.text = resources.getString(R.string.price, hotel.pricePerNight.toString())

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
                    binding.rvReview.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.apply {
                        shimmerReview.stopShimmer()
                        shimmerReview.visibility = View.GONE
                        rvReview.visibility = View.VISIBLE
                    }
                    val data = it.data.data
                    cardReviewAdapter.submitList(data)
                }
                is Result.Error -> {
                    val toast = Toast.makeText(this, it.error, Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }
    }

    private fun setSimilarities() {
        binding.rvSimilarities.apply {
            layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
            addItemDecoration(CardAdapter.MarginItemDecoration(16.dpToPx))
        }
        detailViewModel.getSimilar(profileViewModel.userToken, hotel.id).observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.rvSimilarities.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.apply {
                        shimmerSimilarities.stopShimmer()
                        shimmerSimilarities.visibility = View.GONE
                        rvSimilarities.visibility = View.VISIBLE
                    }
                    val data = it.data.data
                    similaritiesAdapter.submitList(data)
                    binding.rvSimilarities.adapter = similaritiesAdapter
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