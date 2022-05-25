package com.c22ho01.hotelranking.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.SearchAdapter
import com.c22ho01.hotelranking.databinding.ActivitySearchBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter
    private val searchViewModel: SearchViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBar.customSearchBar.requestFocus()
        adapter = SearchAdapter()
        binding.apply {
            rvSearch.adapter = adapter
            rvSearch.layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        setupAction()
    }

    private fun setupAction() {
        binding.searchBar.apply {
            customSearchBar.setEndIconOnClickListener {
                val keyword = etKeyword.text.toString().trim()
                searchHotel(keyword)
            }

            etKeyword.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = etKeyword.text.toString().trim()
                    searchHotel(keyword)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            etKeyword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val keyword = s.toString().trim()
                    searchHotel(keyword)
                }

                override fun afterTextChanged(s: Editable?) {}

            })
        }
    }

    private fun searchHotel(keyword: String) {
        if (keyword.isEmpty()) return
        searchViewModel.searchHotel(keyword).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
}