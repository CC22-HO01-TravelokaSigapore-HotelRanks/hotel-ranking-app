package com.c22ho01.hotelranking.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.c22ho01.hotelranking.R
import com.c22ho01.hotelranking.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback {
            goToLogin()
        }

        setupBtnListener()
    }

    private fun setupBtnListener() {
        binding?.run {
            btnGoToLogin.setOnClickListener {
                goToLogin()
            }
            vtfRegisterUsername.addValidateListener { null }
            vtfRegisterEmail.addValidateListener { null }
            vtfRegisterPassword.addValidateListener { null }
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