package com.c22ho01.hotelranking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ItemSearchBinding

class SearchAdapter : PagingDataAdapter<HotelData, SearchAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HotelData) {
            binding.apply {
                Glide.with(binding.root)
                    .load(data.image.first())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgHotel)
                tvLocation.text = data.neighborhood
                tvHotel.text = data.name
                tvRating.text = data.star.toString()
                tvPrice.text = data.pricePerNight.toString()
                ratingBar.rating = data.star.toFloat()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
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