package com.example.newsapppaging.domain.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapppaging.BuildConfig
import com.example.newsapppaging.data.network.client.NewsApiClient
import com.example.newsapppaging.data.network.model.ArticlesItem

class NewsListPagingSource(
    private val newsApiClient: NewsApiClient,
    private val country: String,
    private val pageSize: Int
) : PagingSource<Int, ArticlesItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        return try {
            val position = params.key ?: 1
            val response = newsApiClient.getNewsByCountry(
                apiKey = BuildConfig.API_KEY,
                country = country,
                pageSize = pageSize,
                page = position
            )

            response.body()?.let { it ->
                return LoadResult.Page(
                    data = it.articles,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (position >= (it.totalResults ?: (1000 / pageSize))) //need to insert the last page from the api call
                        null else position + 1
                )
            }
            return LoadResult.Error(throwable = Throwable("No Item found"))
        } catch (e: Exception) {
            Log.e("error", e.message.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticlesItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
