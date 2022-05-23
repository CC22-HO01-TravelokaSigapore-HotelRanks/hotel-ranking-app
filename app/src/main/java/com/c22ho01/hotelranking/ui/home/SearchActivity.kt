package com.c22ho01.hotelranking.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBar.customSearchBar.requestFocus()
    }
}