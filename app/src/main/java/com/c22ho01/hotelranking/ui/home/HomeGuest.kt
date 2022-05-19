package com.c22ho01.hotelranking.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.databinding.ActivityHomeGuestBinding

class HomeGuest : AppCompatActivity() {

    private lateinit var binding: ActivityHomeGuestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBar.customSearchBar
    }
}