package com.c22ho01.hotelranking.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.ItemForYouBinding
import com.c22ho01.hotelranking.ui.detail.DetailActivity

class ListForYouAdapter : ListAdapter<HotelData, ListForYouAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private var binding: ItemForYouBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HotelData) {
            binding.apply {
                Glide.with(root)
                    .load(data.image.first().trim())
                    .centerCrop()
                    .into(imHotelImg)
                tvLocation.text = data.neighborhood.trim()
                tvHotelName.text = data.name.trim()
                tvRatingScore.text = data.star.toString()
                rbRating.rating = data.star
                val price = data.pricePerNight.toString()
                tvPrice.text = itemView.resources.getString(R.string.price, price)
            }

            itemView.setOnClickListener {
                Intent(itemView.context, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.EXTRA_HOTEL, data)
                    itemView.context.startActivity(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForYouBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : ItemCallback<HotelData>() {
                override fun areItemsTheSame(oldItem: HotelData, newItem: HotelData): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: HotelData, newItem: HotelData): Boolean {
                    return oldItem == newItem
                }

            }
    }
}