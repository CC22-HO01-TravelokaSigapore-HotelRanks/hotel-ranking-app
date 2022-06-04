package com.c22ho01.hotelranking.adapter

import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ItemSearchBinding
import com.c22ho01.hotelranking.ui.detail.DetailActivity

class SearchAdapter : PagingDataAdapter<HotelData, SearchAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HotelData) {
            binding.apply {
                Glide.with(itemView)
                    .load(data.image.first().trim())
                    .centerCrop()
                    .into(imgHotel)
                tvLocation.text = data.neighborhood.trim()
                tvHotel.text = data.name.trim()
                tvRating.text = data.star.toString()
                val price = data.pricePerNight.toString()
                tvPrice.text = itemView.resources.getString(R.string.price, price)
                ratingBar.rating = data.star
            }

            itemView.setOnClickListener {
                Intent(itemView.context, DetailActivity::class.java).also {
                    it.putExtra("extra_hotel", data)
                    itemView.context.startActivity(it)
                }
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
            object : DiffUtil.ItemCallback<HotelData>() {
                override fun areItemsTheSame(oldItem: HotelData, newItem: HotelData): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: HotelData, newItem: HotelData): Boolean {
                    return oldItem == newItem
                }

            }
    }
}