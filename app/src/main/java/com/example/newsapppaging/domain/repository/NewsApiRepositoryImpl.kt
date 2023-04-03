package com.example.newsapppaging.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapppaging.data.NewsRemoteMediator
import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.data.database.ArticleDatabase
import com.example.newsapppaging.data.mapper.ApiMapper
import com.example.newsapppaging.data.network.client.NewsApiClient
import com.example.newsapppaging.util.Constants
import kotlinx.coroutines.flow.Flow

class NewsApiRepositoryImpl(
    private val newsApiClient: NewsApiClient,
    private val apiMapper: ApiMapper,
    private val database: ArticleDatabase
) : NewsApiRepository {

    //using the pager for remote call only
    /*override fun getNewsByCountry(
        country: String,
        pageSize: Int,
    ) = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            prefetchDistance = Constants.PREFETCH_DISTANCE
        ),
        pagingSourceFactory = {
            NewsListPagingSource(
                newsApiClient = newsApiClient,
                country = country,
                pageSize = pageSize
            )
        }
    ).flow*/

    //using the pager for remote and local
    override fun getNewsByCountry(country: String, pageSize: Int): Flow<PagingData<Article>> {

        // appending '%' so we can allow other characters to be before and after the query string
        val pagingSourceFactory = { database.articleDao().getAllArticles() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = Constants.PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = NewsRemoteMediator(
                country = country,
                service = newsApiClient,
                articleDatabase = database,
                apiMapper = apiMapper
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}