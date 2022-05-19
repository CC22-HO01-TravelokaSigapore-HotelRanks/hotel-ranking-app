package com.c22ho01.hotelranking.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.FragmentLoginBinding
import com.c22ho01.hotelranking.ui.DummyActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val tokenViewModel: TokenViewModel by viewModels { factory }

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
            btnLogin.setOnClickListener { loginAccount() }
        }
    }


    private fun setupFieldListener() {
        binding?.run {
            vtfLoginUsername.addValidateListener { loginViewModel.setUsernameValid(it) }
            vtfLoginPassword.addValidateListener { loginViewModel.setPasswordValid(it) }
        }
        loginViewModel.formValid.observe(viewLifecycleOwner) {
            binding?.btnLogin?.isEnabled = it
        }
    }

    private fun loginAccount() {
        loginViewModel.submitLogin(
            userName = binding?.vtfLoginUsername?.getText() ?: "",
            password = binding?.vtfLoginPassword?.getText() ?: "",
        ).run {
            if (this.hasObservers()) this.removeObservers(viewLifecycleOwner)
            this.observe(viewLifecycleOwner) {
                when (it) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        tokenViewModel.setToken(it.data.loginData?.token ?: "")
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_success),
                            Toast.LENGTH_SHORT,
                        ).show()
                        goToHome()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun goToHome() {
        val intent =
            Intent(requireActivity(), DummyActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        startActivity(intent)
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.run {
                progressBarLogin.visibility = View.VISIBLE
                btnLogin.isEnabled = false
            }
        } else {
            binding?.run {
                progressBarLogin.visibility = View.GONE
                btnLogin.isEnabled = loginViewModel.formValid.value ?: false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}