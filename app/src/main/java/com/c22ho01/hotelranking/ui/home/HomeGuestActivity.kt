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
import com.c22ho01.hotelranking.data.local.entity.ProfileEntity
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.databinding.ActivityHomeGuestBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class HomeGuestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeGuestBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val profileViewModel: ProfileViewModel by viewModels { factory }
    private val tokenViewModel: TokenViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityHomeGuestBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(this)
        setContentView(binding.root)
        executeRefreshLogin()

        profileViewModel.getThemeSettings().observe(this) {
            if (it == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home_guest) as NavHostFragment
        navView.setupWithNavController(navHostFragment.navController)

        binding.searchBar.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun executeRefreshLogin() {
        tokenViewModel.getRefreshToken().observe(this) {
            if (it?.isNotBlank() == true) {
                loginViewModel.submitRefreshLogin(it).run {
                    if (this.hasObservers()) this.removeObservers(this@HomeGuestActivity)
                    this.observe(this@HomeGuestActivity) { result ->
                        processLoginObserverResult(result)
                    }
                }
            }
        }
    }

    private fun processLoginObserverResult(result: Result<LoginResponse>) {
        when (result) {
            is Result.Loading -> {
//                showLoading(true)
            }
            is Result.Success -> {
                loginSuccessCallback(result.data)
            }
            is Result.Error -> {
//                showLoading(false)
            }
        }
    }

    private fun loginSuccessCallback(data: LoginResponse) {
        val userId = data.loginData?.userId ?: -1
        profileViewModel.run {
            setProfileID(userId)
            setSavedProfileId(userId)
        }

        tokenViewModel.run {
            setAccessToken(data.loginData?.accessToken ?: "").invokeOnCompletion {
                goToHome()
//                profileViewModel.run {
//                    loadToken()
//                    loadProfile().run {
//                        if (this.hasObservers()) this.removeObservers(this@HomeGuestActivity)
//                        this.observe(this@HomeGuestActivity) {
//                            processProfileObserverResult(it)
//                        }
//                    }
//                }
            }
        }
    }

    private fun processProfileObserverResult(result: Result<ProfileEntity>) {
        when (result) {
            is Result.Loading -> {
//                showLoading(true)
            }
            is Result.Success -> {
                showLoading(false)
                Snackbar.make(
                    binding.root,
                    getString(R.string.login_success),
                    Snackbar.LENGTH_SHORT
                ).show()
                goToHome()
            }
            is Result.Error -> {
//                showLoading(false)
            }
        }
    }

    private fun goToHome() {
        startActivity(
            Intent(
                this@HomeGuestActivity,
                HomeLoggedInActivity::class.java
            ).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
        overridePendingTransition(0, 0)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.clHomeGuestLoading.visibility = android.view.View.VISIBLE
        } else {
            binding.clHomeGuestLoading.visibility = android.view.View.GONE
        }
    }
}