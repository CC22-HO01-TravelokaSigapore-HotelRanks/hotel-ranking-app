package com.c22ho01.hotelranking.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.c22ho01.hotelranking.data.remote.response.review.ReviewData
import com.c22ho01.hotelranking.databinding.ItemSimpleReviewBinding

class CardReviewAdapter : ListAdapter<ReviewData, CardReviewAdapter.ViewHolder>(COMPARATOR) {

    class ViewHolder(private var binding: ItemSimpleReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReviewData) {
            binding.apply {
                itemUsername.text = data.name
                itemReview.text = data.text
                itemRating.rating = data.rating.toFloat()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSimpleReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
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
                    left = spaceSize
                }
                right = spaceSize
            }
        }
    }

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<ReviewData>() {
            override fun areItemsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
                return oldItem == newItem
            }

        }
    }
}