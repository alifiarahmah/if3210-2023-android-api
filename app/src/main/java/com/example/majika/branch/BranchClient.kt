package com.example.majika.branch

import com.example.majika.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BranchClient {
    private const val BASE_URL : String = BuildConfig.BASE_URL
    private var mRetrofit : Retrofit? = null

    fun getInstance(): Retrofit {
        if (mRetrofit == null) {
            mRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return this.mRetrofit!!
    }
}