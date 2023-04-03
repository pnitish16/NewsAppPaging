package com.example.newsapppaging.di

import com.example.newsapppaging.BuildConfig
import com.example.newsapppaging.data.database.ArticleDatabase
import com.example.newsapppaging.data.mapper.ApiMapper
import com.example.newsapppaging.data.mapper.ApiMapperImpl
import com.example.newsapppaging.data.network.client.NewsApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import com.example.newsapppaging.domain.repository.NewsApiRepository
import com.example.newsapppaging.domain.repository.NewsApiRepositoryImpl
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL_NAME = "BASE_URL"
private const val BASE_URL = "https://newsapi.org/v2/"
private const val MAIN_DISPATCHER = "main_dispatcher"
private const val BACKGROUND_DISPATCHER = "background_dispatcher"

val applicationModule = module {
    single(named(BASE_URL_NAME)) {
        BASE_URL
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        val client = OkHttpClient().newBuilder()

        if (BuildConfig.DEBUG) {
            client.addInterceptor(get<HttpLoggingInterceptor>())
        }

        client.build()
    }

    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<String>(named(BASE_URL_NAME)))
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    single {
        get<Retrofit>().create(NewsApiClient::class.java)
    }

    single {
        ArticleDatabase.getInstance(androidContext())
    }


    single<NewsApiRepository> {
        NewsApiRepositoryImpl(
            get(),
            get(),
            get()
        )
    }

    single { Picasso.get() }

    single(named(MAIN_DISPATCHER)) { Dispatchers.Main }

    single(named(BACKGROUND_DISPATCHER)) { Dispatchers.IO }

    single<ApiMapper> {
        ApiMapperImpl()
    }

}