package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ActivityHomeGuestBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeGuestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeGuestBinding
    private lateinit var factory: ViewModelFactory
    private val profileViewModel by viewModels<ProfileViewModel> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        factory = ViewModelFactory.getInstance(this)
        lifecycleScope.launch {
            profileViewModel.getSavedProfileId().collect { id ->
                if (id != null) {
                    startActivity(
                        Intent(
                            this@HomeGuestActivity, HomeLoggedInActivity::class.java
                        ).also {
                            it.putExtra(ProfileFragment.USER_ID, id)
                            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
        }
        binding = ActivityHomeGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home_guest) as NavHostFragment
        navView.setupWithNavController(navHostFragment.navController)

        binding.searchBar.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }
}