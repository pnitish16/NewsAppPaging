package com.example.newsapppaging.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.data.network.model.ArticlesItem
import com.example.newsapppaging.domain.repository.NewsApiRepository
import kotlinx.coroutines.flow.Flow

class NewsViewModel(
    private val repository: NewsApiRepository
) : ViewModel() {

    fun getNewsList(
        country: String,
        pageSize: Int
    ): Flow<PagingData<Article>> {
        val newsList = repository.getNewsByCountry(
            country, pageSize
        ).cachedIn(viewModelScope)
        return newsList
    }

}
