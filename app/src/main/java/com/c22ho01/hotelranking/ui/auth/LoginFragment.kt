package com.c22ho01.hotelranking.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.databinding.FragmentLoginBinding
import com.c22ho01.hotelranking.ui.home.HomeLoggedInActivity
import com.c22ho01.hotelranking.utils.EnvUtils
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val tokenViewModel: TokenViewModel by viewModels { factory }
    private val profileViewModel: ProfileViewModel by viewModels { factory }

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(EnvUtils.getGsoClientId())
            .requestIdToken(EnvUtils.getGsoClientId())
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
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
            btnGoogleSignin.setOnClickListener { loginAccountGoogle() }
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
                processLoginObserverResult(it)
            }
        }
    }


    private fun loginAccountGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        launchGoogleSignIn.launch(signInIntent)
    }

    private val launchGoogleSignIn = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            account.result.serverAuthCode?.let { code ->
                loginViewModel.submitLoginByGoogle(code).run {
                    if (this.hasObservers()) this.removeObservers(viewLifecycleOwner)
                    this.observe(viewLifecycleOwner) {
                        processLoginObserverResult(it)
                    }
                }
            }
        }
    }

    private fun processLoginObserverResult(result: Result<LoginResponse>) {
        when (result) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Success -> {
                showLoading(false)
                loginSuccessCallback(result.data)
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

    private fun loginSuccessCallback(data: LoginResponse) {
        tokenViewModel.setToken(data.loginData?.accessToken ?: "")
        profileViewModel.setProfileID(data.loginData?.userId ?: -1)
        binding?.let { fragment ->
            Snackbar.make(
                fragment.root,
                getString(R.string.login_success),
                Snackbar.LENGTH_SHORT
            ).show()
        }
        goToHome()
    }

    private fun goToHome() {
        val intent =
            Intent(activity, HomeLoggedInActivity::class.java).apply {
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
                pbLoginProgress.visibility = View.VISIBLE
                btnLogin.isEnabled = false
            }
        } else {
            binding?.run {
                pbLoginProgress.visibility = View.GONE
                btnLogin.isEnabled = loginViewModel.formValid.value ?: false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}