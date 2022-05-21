package com.c22ho01.hotelranking.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ActivityHomeLoggedInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeLoggedInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeLoggedInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home_logged_in)
        navView.setupWithNavController(navController)
    }
}