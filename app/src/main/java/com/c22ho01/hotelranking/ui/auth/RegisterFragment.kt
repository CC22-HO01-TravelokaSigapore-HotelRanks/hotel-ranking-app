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
import com.c22ho01.hotelranking.databinding.FragmentRegisterBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.RegisterViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding

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
            vtfRegisterConfirmPassword.addValidateListener(vtfRegisterPassword.getText()) {
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}