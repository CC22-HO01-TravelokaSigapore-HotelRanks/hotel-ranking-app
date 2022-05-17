package com.c22ho01.hotelranking.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.databinding.ActivityHomeLoggedInBinding

class HomeLoggedIn : AppCompatActivity() {

    private lateinit var binding: ActivityHomeLoggedInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}