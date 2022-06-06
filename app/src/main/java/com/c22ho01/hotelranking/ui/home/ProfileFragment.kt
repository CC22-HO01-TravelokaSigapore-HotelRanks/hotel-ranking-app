package com.c22ho01.hotelranking.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        signOut()
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
                        tokenViewModel.deleteToken()
                        profileViewModel.deleteSavedProfileId()
                    }
                    startActivity(Intent(requireActivity(), AuthActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    })
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
                tvEmail.text = profile.email
                tvName.text = profile.name ?: profile.userName
                tvHobby.text = profile.hobby?.joinToString { h ->
                    h?.localizedLabel?.let { getString(it) } ?: "-"
                }
                tvSpecialNeeds.text = profile.specialNeeds?.joinToString { sn ->
                    sn?.localizedLabel?.let { getString(it) } ?: "-"
                }
                tvPrefer.text =
                    if (profile.family == true) getString(R.string.yes) else getString(R.string.no)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val USER_ID = "user_id"
    }
}