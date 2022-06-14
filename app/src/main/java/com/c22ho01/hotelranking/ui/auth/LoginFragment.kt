package com.c22ho01.hotelranking.ui.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.data.remote.response.auth.GoogleConsentGetResponse
import com.c22ho01.hotelranking.data.remote.response.auth.LoginResponse
import com.c22ho01.hotelranking.data.remote.retrofit.APIConfig
import com.c22ho01.hotelranking.databinding.FragmentLoginBinding
import com.c22ho01.hotelranking.ui.home.HomeLoggedInActivity
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.auth.LoginViewModel
import com.c22ho01.hotelranking.viewmodel.profile.ProfileViewModel
import com.c22ho01.hotelranking.viewmodel.utils.TokenViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }
    private val tokenViewModel: TokenViewModel by viewModels { factory }
    private val profileViewModel: ProfileViewModel by viewModels { factory }

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

            btnLogin.setOnClickListener {
                hideSoftKeyboard(it)
                loginAccount()
            }
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

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun loginAccountGoogle() {
        val authGoogleDialog = Dialog(requireContext())
        authGoogleDialog.setContentView(R.layout.dialog_google_auth)

        val webView = authGoogleDialog.findViewById<WebView>(R.id.wv_google_auth)
        webView.apply {
            settings.javaScriptEnabled = true
            settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Mobile Safari/537.36"
            loadUrl(APIConfig.BASE_URL + "user/login/google/consent")
            webViewClient = object : WebViewClient() {

                @Override
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (url?.contains("code=") == true) {
                        val injectScript =
                            "javascript:Android.onResult(200, document.body.children[0].innerText);"
                        webView.loadUrl(injectScript)
                        authGoogleDialog.dismiss()
                    }
                }
            }
            addJavascriptInterface(WebViewResultListener(), "Android")
        }
        authGoogleDialog.show()
    }

    inner class WebViewResultListener {
        @JavascriptInterface
        fun onResult(statusCode: Int, response: String?) {
            val result: GoogleConsentGetResponse =
                Gson().fromJson(response, GoogleConsentGetResponse::class.java)
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    processConsentCode(result)
                }
            }
        }
    }

    fun processConsentCode(data: GoogleConsentGetResponse) {

        data.code?.let { code ->
            loginViewModel.submitLoginByGoogle(code).run {
                if (this.hasObservers()) this.removeObservers(viewLifecycleOwner)
                this.observe(viewLifecycleOwner) {
                    processLoginObserverResult(it)
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
        val userId = data.loginData?.userId ?: -1
        profileViewModel.run {
            setProfileID(userId)
            setSavedProfileId(userId)
        }

        tokenViewModel.run {
            setRefreshToken(data.loginData?.refreshToken ?: "")
            setAccessToken(data.loginData?.accessToken ?: "").invokeOnCompletion {
                goToHome()
            }
        }
    }

    private fun goToHome() {

        startActivity(Intent(requireActivity(), HomeLoggedInActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        })
        requireActivity().finish()
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

    private fun hideSoftKeyboard(view: View) {
        val imm =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}