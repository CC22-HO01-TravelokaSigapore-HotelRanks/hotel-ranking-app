package com.c22ho01.hotelranking.ui.home

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.LoadingStateAdapter
import com.c22ho01.hotelranking.adapter.SearchAdapter
import com.c22ho01.hotelranking.databinding.ActivitySearchBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var factory: ViewModelFactory
    private val searchViewModel: SearchViewModel by viewModels { factory }
    private lateinit var searchJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)
        searchAdapter = SearchAdapter()
        searchJob = Job()

        setupView()
        setupAction()
    }

    private fun setupView() {
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    searchAdapter.retry()
                }
            )
            addItemDecoration(
                SearchAdapter.MarginItemDecoration(48)
            )
        }

        binding.apply {
            searchBar.customSearchBar.requestFocus()
            btnCancel.setOnClickListener { onBackPressed() }
        }
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

            /*etKeyword.addTextChangedListener(object : TextWatcher {
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
                    Timer("SendRequest", false).schedule(1000) {
                        searchHotel(keyword)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            })*/
        }
    }

    private fun searchHotel(keyword: String) {
        searchJob.cancel()
        searchJob = lifecycleScope.launch {
            searchViewModel.searchHotel(keyword).collect {
                searchAdapter.submitData(it)
            }
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}