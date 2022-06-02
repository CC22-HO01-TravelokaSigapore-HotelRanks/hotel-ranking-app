package com.c22ho01.hotelranking.ui.foryou

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c22ho01.hotelranking.databinding.ActivityForYouBinding

class ForYouActivity : AppCompatActivity() {
    private var _binding: ActivityForYouBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForYouBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}