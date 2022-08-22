package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.databinding.ActivityHomeGuestBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeGuestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeGuestBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val profileViewModel: ProfileViewModel by viewModels { factory }
    private val tokenViewModel: TokenViewModel by viewModels { factory }
    private var token: String = ""
    private var keepSplashScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }
        binding = ActivityHomeGuestBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(this)
        setContentView(binding.root)
        getThemeSetting()
        executeRefreshLogin()

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home_guest) as NavHostFragment
        navView.setupWithNavController(navHostFragment.navController)

        binding.searchBar.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun executeRefreshLogin() {
        var isExecuted = false
        tokenViewModel.getRefreshToken().observe(this) {
            if (it?.isNotBlank() == true && !isExecuted) {
                isExecuted = true
                loginViewModel.submitRefreshLogin(it).run {
                    if (this.hasObservers()) this.removeObservers(this@HomeGuestActivity)
                    this.observe(this@HomeGuestActivity) { result ->
                        processLoginObserverResult(result)
                    }
                }
            }
        }
    }

    private fun getThemeSetting() {
        profileViewModel.getThemeSettings().observe(this) {
            if (it == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun processLoginObserverResult(result: Result<LoginResponse>) {
        when (result) {
            is Result.Loading -> {
                keepSplashScreen = true
            }
            is Result.Success -> {
                token = result.data.loginData?.accessToken ?: ""
                val userId = result.data.loginData?.userId ?: -1
                profileViewModel.run {
                    setProfileID(userId)
                }

                tokenViewModel.run {
                    setAccessToken(token).invokeOnCompletion {
                        startActivity(
                            Intent(
                                this@HomeGuestActivity,
                                HomeLoggedInActivity::class.java
                            )
                        )
                        overridePendingTransition(0, 0)
                        finish()
                    }
                }
            }
            else -> {
                keepSplashScreen = false
            }
        }
    }
}