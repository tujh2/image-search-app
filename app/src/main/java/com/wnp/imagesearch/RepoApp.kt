package com.wnp.imagesearch

import android.app.Application
import android.content.Context
import com.wnp.imagesearch.network.ImagesApi

open class RepoApp : Application() {
    //private lateinit var api: ImagesApi.GetImages

    companion object {
        fun from(context: Context): RepoApp {
            return context.applicationContext as RepoApp
        }
        private lateinit var api: ImagesApi.GetImages
        fun getApi(): ImagesApi.GetImages {
            return api
        }
    }

    override fun onCreate() {
        super.onCreate()
        api = ImagesApi().getApi()
    }
}