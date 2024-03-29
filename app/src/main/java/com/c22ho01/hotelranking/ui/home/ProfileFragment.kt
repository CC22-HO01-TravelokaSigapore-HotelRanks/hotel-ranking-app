package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.FragmentProfileBinding
import com.c22ho01.hotelranking.ui.auth.AuthActivity
import com.c22ho01.hotelranking.ui.profile.ProfileCustomizeActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val profileViewModel: ProfileViewModel by viewModels { factory }
    private val tokenViewModel by viewModels<TokenViewModel> { factory }

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
        if (profileViewModel.getCurrentProfile().value?.id == null) {
            loadProfile()
        } else {
            initializeProfileData()
        }

        profileViewModel.getThemeSettings().observe(viewLifecycleOwner) {
            isDarkMode(it)
        }

        changeLanguage()
        switchTheme()
        signOut()
    }

    private fun changeLanguage() {
        binding?.languageButton?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        when (Locale.getDefault().country) {
            "US" -> binding?.tvLanguage?.text = getString(R.string.english_us)
            "ID" -> binding?.tvLanguage?.text = getString(R.string.indonesian)
        }
    }

    private fun signOut() {
        binding?.btnSignOut?.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.logout_title))
                .setMessage(resources.getString(R.string.logout_message))
                .setNegativeButton(resources.getString(R.string.logout_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.logout_ok)) { _, _ ->
                    lifecycleScope.launch {
                        tokenViewModel.run {
                            deleteAccessToken()
                            deleteRefreshToken()
                        }
                        profileViewModel.deleteSavedProfileId()
                        profileViewModel.setCurrentProfile(null)
                    }
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                }
                .show()
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
                    showSnackbar(result.error)
                }
            }
        }
    }

    private fun initializeProfileData() {
        profileViewModel.getCurrentProfile().observe(viewLifecycleOwner) { profile ->
            binding?.run {
                tvEmail.text = profile?.email
                tvName.text = profile?.name ?: profile?.userName
                tvHobby.text = profile?.hobby?.joinToString { h ->
                    h?.localizedLabel?.let { getString(it) } ?: "-"
                }
                tvSpecialNeeds.text = profile?.specialNeeds?.joinToString { sn ->
                    sn?.localizedLabel?.let { getString(it) } ?: "-"
                }
                tvPrefer.text =
                    if (profile?.family == true) getString(R.string.yes) else getString(R.string.no)
                btnGoToCustomizeProfile.setOnClickListener {
                    val intent = Intent(activity, ProfileCustomizeActivity::class.java).apply {
                        putExtra(ProfileCustomizeActivity.EXTRA_PROFILE, profile)
                    }
                    startActivity(intent)
                }
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

    private fun showSnackbar(text: String) {
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.let {
            Snackbar.make(it, text, Snackbar.LENGTH_SHORT).apply {
                anchorView = bottomNavigationView
            }.show()
        }
    }

    private fun switchTheme() {
        binding?.switchTheme?.setOnCheckedChangeListener { _, isChecked ->
            profileViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun isDarkMode(state: Boolean) {
        if (state) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding?.switchTheme?.isChecked = true
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding?.switchTheme?.isChecked = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}