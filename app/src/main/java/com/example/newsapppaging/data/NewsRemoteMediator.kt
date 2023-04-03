package com.example.newsapppaging.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapppaging.BuildConfig
import com.example.newsapppaging.data.database.Article
import com.example.newsapppaging.data.database.ArticleDatabase
import com.example.newsapppaging.data.database.RemoteKeys
import com.example.newsapppaging.data.mapper.ApiMapper
import com.example.newsapppaging.data.network.client.NewsApiClient
import retrofit2.HttpException
import java.io.IOException


private const val NEWS_API_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val country: String,
    private val service: NewsApiClient,
    private val articleDatabase: ArticleDatabase,
    private val apiMapper: ApiMapper
) : RemoteMediator<Int, Article>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: NEWS_API_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val apiResponse =
                service.getNewsByCountry(BuildConfig.API_KEY, country, state.config.pageSize, page)

            val articles = apiResponse.body()?.articles
            val endOfPaginationReached = articles?.isEmpty() ?: false
            articleDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    articleDatabase.remoteKeysDao().clearRemoteKeys()
                    articleDatabase.articleDao().clearArticles()
                }
                val prevKey = if (page == NEWS_API_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = articles?.map {
                    RemoteKeys(articleUrl = it.url ?: "", prevKey = prevKey, nextKey = nextKey)
                }
                articleDatabase.remoteKeysDao().insertAll(keys!!)
                articleDatabase.articleDao()
                    .insertAll(articles.map { apiMapper.mapApiToDatabase(it) })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Article>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                // Get the remote keys of the last item retrieved
                articleDatabase.remoteKeysDao().remoteKeysArticleURl(article.url)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Article>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article ->
                // Get the remote keys of the first items retrieved
                articleDatabase.remoteKeysDao().remoteKeysArticleURl(article.url)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Article>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.url?.let { url ->
                articleDatabase.remoteKeysDao().remoteKeysArticleURl(url)
            }
        }
    }
}