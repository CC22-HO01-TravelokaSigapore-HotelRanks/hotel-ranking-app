package com.c22ho01.hotelranking.ui.home

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.SearchAdapter
import com.c22ho01.hotelranking.data.Result
import com.c22ho01.hotelranking.databinding.ActivitySearchBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val searchViewModel: SearchViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchAdapter = SearchAdapter()
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
            setHasFixedSize(false)
            addItemDecoration(
                SearchAdapter.MarginItemDecoration(48)
            )
//                .withLoadStateFooter(LoadingStateAdapter { searchAdapter.retry() })
        }

        binding.apply {
            searchBar.customSearchBar.requestFocus()
            btnCancel.setOnClickListener { onBackPressed() }
        }
        setupAction()
    }

    private fun setupAction() {
        binding.searchBar.apply {
            etKeyword.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = etKeyword.text.toString().trim()
                    searchHotel(keyword)
                    hideSoftKeyboard(v)
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
                    Log.e("CEK TEXT", keyword)
                    searchHotel(keyword)
                }

                override fun afterTextChanged(s: Editable?) {}

            })
        }
    }

    private fun searchHotel(keyword: String) {
        searchViewModel.hotelSearch(keyword).observe(this) {
            when (it) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val data = it.data.data
                    searchAdapter.submitList(data)
                }
                else -> {

                }
            }
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}