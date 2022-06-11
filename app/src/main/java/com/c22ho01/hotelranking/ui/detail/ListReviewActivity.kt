package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.ListReviewAdapter
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityListReviewBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.review.ReviewViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListReviewBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var hotel: HotelData
    private val reviewViewModel: ReviewViewModel by viewModels { factory }
    private lateinit var itemReviewAdapter: ListReviewAdapter
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        factory = ViewModelFactory.getInstance(this)
        job = Job()
        hotel = intent.getParcelableExtra<HotelData>(DetailActivity.EXTRA_HOTEL) as HotelData
        itemReviewAdapter = ListReviewAdapter()
        setData()
        setReviews()
    }

    private fun setData() {
        binding.hotelName.text = hotel.name
        binding.ratingText.text = resources.getString(R.string.rating, hotel.star.toString())
    }

    private fun setReviews() {
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(this@ListReviewActivity)
            adapter = itemReviewAdapter
            addItemDecoration(
                ListReviewAdapter.MarginItemDecoration(0)
            )
        }
        job.cancel()
        job = lifecycleScope.launch {
            reviewViewModel.getHotelReviewPaging(hotel.id).collect {
                itemReviewAdapter.submitData(it)
            }
        }
    }
}