package com.c22ho01.hotelranking.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.c22ho01.hotelranking.databinding.FragmentHomeLoggedInBinding

class HomeLoggedInFragment : Fragment() {

    private var _binding: FragmentHomeLoggedInBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeLoggedInBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO: add some logic
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}