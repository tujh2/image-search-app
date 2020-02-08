package com.wnp.imagesearch.network

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class ImagesApi() {
    private val api: GetImages

    init {
        val mOkHttpClient = OkHttpClient().newBuilder().build()
        val retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(HttpUrl.Builder().scheme("https").host("www.google.com").build())
            .client(mOkHttpClient)
            .build()
        api = retrofit.create(GetImages::class.java)
    }

    fun getApi(): GetImages {
        return api
    }

    interface GetImages {
        @GET("/search?tbm=isch&async=_fmt:pc&asearch=ichunk")
        fun requestImages(@Query("q") query: String, @Query("start") page: Int): Call<ResponseBody>
    }
}