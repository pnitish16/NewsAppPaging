package com.example.newsapppaging.data.network.client

import com.example.newsapppaging.data.network.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiClient {

    @GET("top-headlines")
    suspend fun getNewsByCountry(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): Response<ApiResponse>

}