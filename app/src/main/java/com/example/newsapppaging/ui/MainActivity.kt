package com.example.newsapppaging.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.databinding.ActivityMainBinding
import com.example.newsapppaging.ui.adapter.NewsListPagingAdapter
import com.example.newsapppaging.ui.viewmodel.NewsViewModel
import com.example.newsapppaging.util.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val newsViewModel by viewModel<NewsViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsListPagingAdapter: NewsListPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupViews()

        setupObservers()
    }
    private fun setupObservers() {

        lifecycleScope.launch {
            newsViewModel.getNewsList("in", Constants.PAGE_SIZE).collect {
                newsListPagingAdapter.submitData(it)
            }
        }

        newsListPagingAdapter.addLoadStateListener { loadState ->
            binding.errorMsg.isVisible = loadState.refresh is LoadState.Error
            binding.loadingProgress.isVisible = loadState.refresh is LoadState.Loading
        }
    }

    private fun setupViews() {
        binding.apply {
            lifecycleOwner = this@MainActivity

            newsListPagingAdapter =
                NewsListPagingAdapter(
                    object : NewsListPagingAdapter.OnItemClickListener {
                        override fun onItemClick(articlesItem: Article) {
                            //open detail page
                        }
                    })
            newsListRv.apply {
                adapter = newsListPagingAdapter
            }
        }
    }
}