package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ActivityHomeLoggedInBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class HomeLoggedInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeLoggedInBinding
    private val tokenViewModel by viewModels<TokenViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workManager = WorkManager.getInstance(this)
        startPeriodicTask()

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home_logged_in) as NavHostFragment
        navView.setupWithNavController(navHostFragment.navController)

        binding.searchBar.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun startPeriodicTask() {
        tokenViewModel.apply {
            getRefreshToken().observe(this@HomeLoggedInActivity) { refreshToken ->
                if (refreshToken?.isNotBlank() == true) {
                    val data = Data.Builder()
                        .putString(TokenWorker.REFRESH_TOKEN, refreshToken)
                        .build()

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val periodicRefreshToken = PeriodicWorkRequest.Builder(
                        TokenWorker::class.java,
                        15,
                        TimeUnit.MINUTES
                    )
                        .setInputData(data)
                        .setConstraints(constraints)
                        .addTag(TOKEN_WORKER)
                        .build()

                    workManager.enqueueUniquePeriodicWork(
                        TOKEN_WORKER,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicRefreshToken
                    )
                }
            }
            if (TokenWorker.NEW_ACCESS_TOKEN?.isNotBlank() == true) {
                setAccessToken(TokenWorker.NEW_ACCESS_TOKEN.toString())
            }
        }
    }

    companion object {
        private val TOKEN_WORKER = TokenWorker::class.java.simpleName
    }
}