package com.c22ho01.hotelranking.adapter

import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.remote.response.hotel.HotelData
import com.c22ho01.hotelranking.databinding.CardHotelWithLocationBinding
import com.c22ho01.hotelranking.ui.detail.DetailActivity
import com.c22ho01.hotelranking.ui.home.HomeLoggedInFragment.Companion.USER_LAT
import com.c22ho01.hotelranking.ui.home.HomeLoggedInFragment.Companion.USER_LONG
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class CardLocationAdapter : ListAdapter<HotelData, CardLocationAdapter.ViewHolder>(COMPARATOR) {

    class ViewHolder(private var binding: CardHotelWithLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var distance: Double? = null
        fun bind(data: HotelData) {
            binding.apply {
                Glide.with(root)
                    .load(data.image.first().trim())
                    .centerCrop()
                    .into(imageHotel)
                val userLatLng = USER_LAT?.let { lat ->
                    USER_LONG?.let { long ->
                        LatLng(lat, long)
                    }
                }
                val hotelLatLng = LatLng(data.latitude, data.longitude)
                if (USER_LAT != null && USER_LONG != null) {
                    distance = SphericalUtil.computeDistanceBetween(
                        userLatLng,
                        hotelLatLng
                    ).div(1000)
                }
                tvLocation.text = data.neighborhood.trim()
                tvDistance.text =
                    itemView.resources.getString(R.string.distance, distance)
                tvHotel.text = data.name.trim()
                tvRating.text = data.star.toString()
                ratingbar.rating = data.star
                val price = data.pricePerNight.toString()
                tvPrice.text = itemView.resources.getString(R.string.price, price)
            }

            itemView.setOnClickListener {
                Intent(itemView.context, DetailActivity::class.java).also {
                    it.putExtra("extra_hotel", data)
                    itemView.context.startActivity(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardHotelWithLocationBinding.inflate(
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
        val COMPARATOR = object : DiffUtil.ItemCallback<HotelData>() {
            override fun areItemsTheSame(oldItem: HotelData, newItem: HotelData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HotelData, newItem: HotelData): Boolean {
                return oldItem == newItem
            }

        }
    }
}