package com.wnp.imagesearch

import android.app.Application
import android.content.Context
import android.provider.MediaStore
import com.wnp.imagesearch.network.ImagesApi

open class RepoApp : Application() {
    private lateinit var api: ImagesApi.GetImages

    companion object {
        fun from(context: Context): RepoApp {
            return context.applicationContext as RepoApp
        }

    }

    override fun onCreate() {
        super.onCreate()
        api = ImagesApi().getApi()
    }

    fun getApi(): ImagesApi.GetImages {
        return api
    }
}