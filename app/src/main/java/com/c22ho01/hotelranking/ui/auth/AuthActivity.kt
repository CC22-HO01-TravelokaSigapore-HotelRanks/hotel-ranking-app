package com.c22ho01.hotelranking.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val mFragmentManager = supportFragmentManager
        val mLoginFragment = LoginFragment()
        mFragmentManager.commit {
            add(R.id.auth_fragment_container, mLoginFragment, LoginFragment::class.java.simpleName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}