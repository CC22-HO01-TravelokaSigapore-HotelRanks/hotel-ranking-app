package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.c22ho01.hotelranking.databinding.FragmentHomeGuestBinding
import com.c22ho01.hotelranking.ui.auth.AuthActivity
import com.c22ho01.hotelranking.viewmodel.hotel.HomeViewModel

class HomeGuestFragment : Fragment() {

    private var _binding: FragmentHomeGuestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeGuestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.loginHereBtn.setOnClickListener {
            Intent(activity, AuthActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}