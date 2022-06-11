package com.c22ho01.hotelranking.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.response.review.ReviewData
import com.c22ho01.hotelranking.databinding.ItemDetailReviewBinding
import com.c22ho01.hotelranking.utils.DateUtils

class ListReviewAdapter :
    PagingDataAdapter<ReviewData, ListReviewAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ViewHolder(private val binding: ItemDetailReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReviewData) {
            binding.apply {
                tvRating.text =
                    itemView.resources.getString(R.string.rating, data.rating.toFloat().toString())
                tvReview.text = data.text
                tvUsername.text = data.name
                ratingBar.rating = data.rating.toFloat()
                tvTime.text = DateUtils.formatDateToTimeAgo(data.dates)
            }
        }
    }

    class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceSize
                }
                bottom = spaceSize
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ReviewData>() {
                override fun areItemsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
                    return oldItem == newItem
                }

            }
    }
}