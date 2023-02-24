package com.example.majika.daftarmenu

import com.example.majika.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object MenuClient {
    private const val BASE_URL:String = BuildConfig.BASE_URL //localhost
    private var mRetrofit: Retrofit? = null

    fun getInstance():Retrofit{
        //bikin retrofit client
        if(mRetrofit==null) {
            mRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return this.mRetrofit!!
    }

}