package com.example.newsapppaging.application

import android.app.Application
import com.example.newsapppaging.di.applicationModule
import com.example.newsapppaging.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NewsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@NewsApplication)
            modules(applicationModule, presentationModule)
        }
    }
}