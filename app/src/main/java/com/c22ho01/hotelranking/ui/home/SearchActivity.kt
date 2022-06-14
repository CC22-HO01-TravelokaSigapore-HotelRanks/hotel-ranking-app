package com.c22ho01.hotelranking.ui.home

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22ho01.hotelranking.adapter.SearchAdapter
import com.c22ho01.hotelranking.databinding.ActivitySearchBinding
import com.c22ho01.hotelranking.viewmodel.ViewModelFactory
import com.c22ho01.hotelranking.viewmodel.hotel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
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

        setupAdapter()
        setupView()
        setupAction()
    }

    private fun setupAdapter() {
        searchAdapter.addLoadStateListener { loadState ->
            val loading =
                when {
                    loadState.refresh is LoadState.Loading -> true
                    loadState.prepend is LoadState.Loading -> true
                    loadState.append is LoadState.Loading -> true
                    else -> false
                }
            if (loading) {
                binding.progressIndicator.visibility = View.VISIBLE
            } else {
                binding.progressIndicator.visibility = View.GONE
            }

            val idle = when {
                loadState.refresh is LoadState.NotLoading -> true
                loadState.prepend is LoadState.NotLoading -> true
                loadState.append is LoadState.NotLoading -> true
                else -> false
            }
            if (!loading && idle && searchAdapter.itemCount == 0) {
                showEmpty(true)
            } else {
                showEmpty(false)
            }

            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            if (error?.error?.message?.isNotBlank() == true) {
                Snackbar.make(
                    binding.root,
                    error.error.message.toString(),
                    Snackbar.LENGTH_SHORT,
                ).show()
            }
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        when (isEmpty) {
            true -> {
                binding.apply {
                    rvSearch.visibility = View.GONE
                    tvSearchNoResult.visibility = View.VISIBLE
                }
            }
            false -> {
                binding.apply {
                    rvSearch.visibility = View.VISIBLE
                    tvSearchNoResult.visibility = View.GONE
                }
            }
        }
    }

    private fun setupView() {
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
            addItemDecoration(
                SearchAdapter.MarginItemDecoration(48)
            )
        }

        binding.apply {
            etKeyword.requestFocus()
            btnCancel.setOnClickListener { onBackPressed() }
        }
    }

    private fun setupAction() {
        binding.apply {
            etKeyword.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    binding.apply {
                        progressIndicator.visibility = View.VISIBLE
                    }
                    val keyword = etKeyword.text.toString().trim()
                    searchHotel(keyword)
                    hideSoftKeyboard(v)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
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