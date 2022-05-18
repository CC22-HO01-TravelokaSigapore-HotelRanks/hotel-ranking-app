package com.c22ho01.hotelranking.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.FragmentLoginBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val viewModel: LoginViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFieldListener()
        setupBtnListener()
    }

    private fun setupBtnListener() {
        binding?.run {
            btnGoToCreateAcc.setOnClickListener {
                goToRegister()
            }
        }
    }

    private fun setupFieldListener() {
        binding?.run {
            vtfLoginEmail.addValidateListener { viewModel.setEmailValid(it) }
            vtfLoginPassword.addValidateListener { viewModel.setPasswordValid(it) }
        }
        viewModel.formValid.observe(viewLifecycleOwner) {
            binding?.btnLogin?.isEnabled = it
        }
    }

    private fun goToRegister() {
        val mFragmentManager = parentFragmentManager
        val mDestFragment = RegisterFragment()
        val mDestFragmentTag = RegisterFragment::class.java.simpleName

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