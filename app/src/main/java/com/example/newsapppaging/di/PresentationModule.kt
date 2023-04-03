package com.example.newsapppaging.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.newsapppaging.ui.viewmodel.NewsViewModel

val presentationModule = module  {
    viewModel { NewsViewModel(get()) }
}
