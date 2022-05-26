package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.FragmentProfileBinding
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val profileViewModel: ProfileViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        setupButtonListener()
    }

    private fun setupButtonListener() {
        binding?.btnGoToCustomizeProfile?.setOnClickListener {
            val intent = Intent(activity, ProfileCustomizeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun loadProfile() {
        profileViewModel.loadProfile().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    initializeProfileData()
                    showLoading(false)
                }
                is Result.Error -> {
                    showLoading(false)
                    binding?.let { fragment ->
                        Snackbar.make(
                            fragment.root,
                            result.error,
                            Snackbar.LENGTH_LONG,
                        ).show()
                    }
                }
            }
        }
    }

    private fun initializeProfileData() {
        profileViewModel.getCurrentProfile().observe(viewLifecycleOwner) { profile ->
            binding?.run {
                tvEmail.text = profile.email
                tvName.text = profile.name
                tvHobby.text = profile.hobby?.joinToString(", ") { h ->
                    h?.localizedLabel?.let { getString(it) } ?: "-"
                }
                tvSpecialNeeds.text = profile.specialNeeds?.joinToString(", ") { sn ->
                    sn?.localizedLabel?.let { getString(it) } ?: "-"
                }
                tvPrefer.text =
                    if (profile.family == true) getString(R.string.yes) else getString(R.string.no)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.run {
                pbProfile.visibility = View.VISIBLE
            }
        } else {
            binding?.run {
                pbProfile.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}