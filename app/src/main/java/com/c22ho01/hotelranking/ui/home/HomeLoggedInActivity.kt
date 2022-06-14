package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.c22ho01.hotelranking.viewmodel.utils.TokenWorker.Companion.NEW_TOKEN
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class HomeLoggedInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeLoggedInBinding
    private val tokenViewModel by viewModels<TokenViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var workManager: WorkManager
    private lateinit var periodicRefreshToken: PeriodicWorkRequest
    private lateinit var token: String

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

                    periodicRefreshToken = PeriodicWorkRequest.Builder(
                        TokenWorker::class.java,
                        PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                        TimeUnit.MILLISECONDS
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

                    workManager.getWorkInfoByIdLiveData(periodicRefreshToken.id)
                        .observe(this@HomeLoggedInActivity) {
                            Log.e("STATE", it.outputData.getString(NEW_TOKEN) ?: "kosong")
                            Log.e("STATE", it.outputData.getString(NEW_TOKEN) ?: "kosong")
                            if (it != null && it.state.isFinished) {
                                token = it.outputData.getString(NEW_TOKEN) ?: "kosong"
                                tokenViewModel.setAccessToken(token)
                            }
                        }
                }
            }
        }
    }

    companion object {
        private val TOKEN_WORKER: String = TokenWorker::class.java.simpleName
    }
}