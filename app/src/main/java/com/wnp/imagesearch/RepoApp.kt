package com.wnp.imagesearch

import android.app.Application
import android.content.Context
import com.wnp.imagesearch.network.ImagesApi

open class RepoApp : Application() {
    companion object {
        private lateinit var app: Application

        fun from(context: Context): RepoApp {
            return context.applicationContext as RepoApp
        }
        private lateinit var api: ImagesApi.GetImages
        fun getApi(): ImagesApi.GetImages {
            return api
        }

        fun gerAppContext() : Context{
            return app.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        api = ImagesApi().getApi()
    }
}