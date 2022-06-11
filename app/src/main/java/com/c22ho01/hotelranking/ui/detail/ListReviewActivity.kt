package com.c22ho01.hotelranking.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.adapter.ListReviewAdapter
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ActivityListReviewBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.review.ReviewViewModel
import com.google.android.material.snackbar.Snackbar
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
        binding.topAppBar.apply {
            title = resources.getString(R.string.review)
            setNavigationOnClickListener {
                finish()
            }
        }
        itemReviewAdapter = ListReviewAdapter()
        setupAdapter()
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

    private fun setupAdapter() {
        itemReviewAdapter.addLoadStateListener { loadState ->
            val loading =
                when {
                    loadState.refresh is LoadState.Loading -> true
                    loadState.prepend is LoadState.Loading -> true
                    loadState.append is LoadState.Loading -> true
                    else -> false
                }
            if (loading) {
                binding.pbReview.visibility = View.VISIBLE
            } else {
                binding.pbReview.visibility = View.GONE
            }
            val idle = when {
                loadState.refresh is LoadState.NotLoading -> true
                loadState.prepend is LoadState.NotLoading -> true
                loadState.append is LoadState.NotLoading -> true
                else -> false
            }
            if (!loading && idle && itemReviewAdapter.itemCount == 0) {
                showEmpty(true)
            } else {
                showEmpty(false)
            }

            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            if (error != null) {
                Snackbar.make(
                    binding.root,
                    error.error.message.toString(),
                    Snackbar.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        when (isEmpty) {
            true -> {
                binding.apply {
                    rvReviews.visibility = View.GONE
                    tvReviewNoResult.visibility = View.VISIBLE
                }
            }
            false -> {
                binding.apply {
                    rvReviews.visibility = View.VISIBLE
                    tvReviewNoResult.visibility = View.GONE
                }
            }
        }
    }
}