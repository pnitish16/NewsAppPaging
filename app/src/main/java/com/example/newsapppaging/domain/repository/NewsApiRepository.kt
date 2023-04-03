package com.example.newsapppaging.domain.repository

import androidx.paging.PagingData
import com.example.newsapppaging.data.database.Article
import kotlinx.coroutines.flow.Flow

interface NewsApiRepository {
    fun getNewsByCountry(country: String, pageSize: Int): Flow<PagingData<Article>>
}