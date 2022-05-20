package com.c22ho01.hotelranking.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.FragmentRegisterBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.RegisterViewModel
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val viewModel: RegisterViewModel by viewModels { factory }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        factory = ViewModelFactory.getInstance(requireContext())

        activity?.onBackPressedDispatcher?.addCallback {
            goToLogin()
        }
        setupFieldListener()
        setupBtnListener()
    }

    private fun setupFieldListener() {
        binding?.run {
            vtfRegisterUsername.addValidateListener {
                viewModel.setUsernameValid(it)
            }
            vtfRegisterEmail.addValidateListener {
                viewModel.setEmailValid(it)
            }
            vtfRegisterPassword.addValidateListener {
                viewModel.setPasswordValid(it)
            }
            vtfRegisterConfirmPassword.addValidateListener(matchValidateableTextFieldView = vtfRegisterPassword) {
                viewModel.setConfirmPasswordValid(it)
            }
        }

        viewModel.formValid.observe(viewLifecycleOwner) {
            binding?.btnRegister?.isEnabled = it
        }
    }

    private fun setupBtnListener() {
        binding?.run {
            btnGoToLogin.setOnClickListener {
                goToLogin()
            }
            btnRegister.setOnClickListener {
                registerAccount()
            }
        }
    }

    private fun registerAccount() {
        viewModel.submitRegister(
                userName = binding?.vtfRegisterUsername?.getText() ?: "",
                email = binding?.vtfRegisterEmail?.getText() ?: "",
                password = binding?.vtfRegisterPassword?.getText() ?: "",
        ).run {
            if (this.hasObservers()) this.removeObservers(viewLifecycleOwner)
            this.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        binding?.let { fragment ->
                            Snackbar.make(
                                    fragment.root,
                                    getString(R.string.register_success),
                                    Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        goToLogin()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        binding?.let { fragment ->
                            Snackbar.make(
                                    fragment.root,
                                    it.error,
                                    Snackbar.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun goToLogin() {
        val mFragmentManager = parentFragmentManager
        val mDestFragment = LoginFragment()
        val mDestFragmentTag = LoginFragment::class.java.simpleName

        mFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(
                    R.id.auth_fragment_container,
                    mDestFragment,
                    mDestFragmentTag,
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.run {
                pbRegisterProgress.visibility = View.VISIBLE
                btnRegister.isEnabled = false
            }
        } else {
            binding?.run {
                pbRegisterProgress.visibility = View.GONE
                btnRegister.isEnabled = viewModel.formValid.value ?: false
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}